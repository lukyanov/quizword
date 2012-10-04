package com.lingvapps.quizword.renew;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CardPager extends ViewPager {
    
    private Context context;
    private int mode;
    private CardSet cardSet;
    private CardLayout currentCardLayout;
    
    private CardPagerAdapter cardPagerAdapter;

    public CardPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        
        this.setPageMargin(0);
        
        cardPagerAdapter = new CardPagerAdapter();
        reinit();
    }
    
    public void reinit() {
        setAdapter(cardPagerAdapter);
    }
    
    public void setMode(int mode) {
        this.mode = mode;
    }
    
    public void setCardSet(CardSet cardSet) {
        this.cardSet = cardSet;
    }
    
    public CardLayout getCurrentCard() {
        return currentCardLayout;
    }
    
    public void setCurrentCard(int cardIndex) {
        this.setCurrentItem(cardIndex);
    }
    
    private class CardPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return cardSet.size();
        }
        
        @Override
        public Object instantiateItem(View collection, int position) {
            Card card = cardSet.getCard(position);
            CardLayout layout = new CardLayout(context, card, mode);
            ((ViewPager) collection).addView(layout, 0);
            return layout;
        }

        @Override
        public void destroyItem(View collection, int position, Object view) {
            ((ViewPager) collection).removeView((View) view);
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View)object);
        }
        
        public void setPrimaryItem(View container, int position, Object object) {
            currentCardLayout = (CardLayout) object;
        }


//        @Override
//        public void finishUpdate(View arg0) {}
        
 //       @Override
 //       public void restoreState(Parcelable arg0, ClassLoader arg1) {}

 //       @Override
 //       public Parcelable saveState() {
 //           return null;
 //       }

//        @Override
//        public void startUpdate(View arg0) {}
        
    }
}
