package com.lingvapps.quizword.utils;

import java.io.FileInputStream;
import java.util.Vector;

import com.lingvapps.quizword.core.Card;
import com.lingvapps.quizword.core.CardSet;
import com.lingvapps.quizword.renew.CardLayout;
import com.lingvapps.quizword.tasks.RetrieveSpeechTask;

import android.content.Context;
import android.media.MediaPlayer;

public class TextToSpeech {

    static private MediaPlayer mediaPlayer = null;

    public interface OnCompletionListener {
        void onSuccess();

        void onFailure();
    }

    static public void utterCard(final Context context,
            final CardLayout cardLayout, int mode,
            final OnCompletionListener listener) {

        final Card card = cardLayout.getCard();
        final CardSet cardSet = card.getCardSet();
        final String id = card.getId().toString();
        final Vector<String> playList = new Vector<String>();

        final RetrieveSpeechTask.OnPostExecuteListener<String> singleListener = new RetrieveSpeechTask.OnPostExecuteListener<String>() {
            public void onSuccess(String filePath) {
                playList.add(filePath);
                listener.onSuccess();
                play(context, playList, 0);
            }

            public void onFailure() {
                listener.onFailure();
            }
        };

        RetrieveSpeechTask.OnPostExecuteListener<String> doubleListener = new RetrieveSpeechTask.OnPostExecuteListener<String>() {
            public void onSuccess(String filePath) {
                playList.add(filePath);
                runSpeechTask(context, id + "_definition",
                        stripBrackets(card.getDefinition()), cardSet.getLangDefinitions(),
                        singleListener);
            }

            public void onFailure() {
                listener.onFailure();
            }
        };

        if (mode == CardLayout.MODE_SINGLE_SIDE) {
            runSpeechTask(context, id + "_term", stripBrackets(card.getTerm()),
                    cardSet.getLangTerms(), doubleListener);
        } else {
            runSpeechTask(context, id + "_" + cardLayout.getCurrentSideType(),
                    stripBrackets(cardLayout.getCurrentSideText()),
                    cardLayout.getCurrentSideLang(), singleListener);
        }
    }
    
    static public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    static private void runSpeechTask(Context context, String id, String text,
            String lang,
            RetrieveSpeechTask.OnPostExecuteListener<String> listener) {
        RetrieveSpeechTask task = new RetrieveSpeechTask(context);
        task.setOnPostExecuteListener(listener);
        task.execute(id, text, lang);
    }

    static private void play(final Context context,
            final Vector<String> playList, final int index) {
        String filePath = playList.get(index);
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            } catch (Exception e) { }
            FileInputStream fileInputStream = new FileInputStream(filePath);
            mediaPlayer.setDataSource(fileInputStream.getFD()); 
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                    if (index < playList.size() - 1) {
                        play(context, playList, index + 1);
                    }
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private String stripBrackets(String text) {
        return text.replaceAll("\\([^\\(\\)]*\\)", "");
    }
}
