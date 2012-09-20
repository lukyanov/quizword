package com.lingvapps.quizword;

import java.io.File;
import java.util.Vector;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

public class TextToSpeech {

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
                        card.getDefinition(), cardSet.getLangDefinitions(),
                        singleListener);
            }

            public void onFailure() {
                listener.onFailure();
            }
        };

        if (mode == CardLayout.MODE_SINGLE_SIDE) {
            runSpeechTask(context, id + "_term", card.getTerm(),
                    cardSet.getLangTerms(), doubleListener);
        } else {
            runSpeechTask(context, id + "_" + cardLayout.getCurrentSideType(),
                    cardLayout.getCurrentSideText(),
                    cardLayout.getCurrentSideLang(), singleListener);
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
            File file = new File(filePath);
            MediaPlayer mp = MediaPlayer.create(context, Uri.fromFile(file));
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    if (index < playList.size() - 1) {
                        play(context, playList, index + 1);
                    }
                }
            });
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
