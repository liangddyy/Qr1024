package com.fjut.qr1024.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fjut.qr1024.R;
import com.fjut.qr1024.model.Card;

import java.util.List;

/**
 * 适配器
 *
 * @author liangddyy
 * @created 2016/9/8
 */
public class CardAdapter extends BaseAdapter{

    private Context context;
    private List<Card> cards;

    public CardAdapter(Context context, List<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @Override
    public int getCount() {
        if (cards == null) {
            return 0;
        }
        return cards.size();
    }

    @Override
    public Object getItem(int i) {
        return cards.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CardHolder cardHolder = null;
        if (view == null) {
            cardHolder = new CardHolder();
            view = View.inflate(context, R.layout.item_card, null);
            cardHolder.tvName = (TextView) view.findViewById(R.id.ItemTitle);
            cardHolder.tvPhone = (TextView) view.findViewById(R.id.ItemText);
            view.setTag(cardHolder);
        } else {
            cardHolder = (CardHolder) view.getTag();
        }

        cardHolder.tvName.setText(cards.get(i).getName()+"");
        cardHolder.tvPhone.setText(cards.get(i).getPhone()+"");
        return view;
    }

    private class CardHolder {
        public TextView tvName;
        public TextView tvPhone;
    }
}
