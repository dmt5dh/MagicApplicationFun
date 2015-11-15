package arashincleric.com.magicapplicationfun;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<Bitmap> imageList;

    public ViewPagerAdapter(Context context){
        this.context = context;
        imageList = new ArrayList<Bitmap>();
    }

    public ViewPagerAdapter(Context context, ArrayList<Bitmap> b){
        this.context = context;
        imageList = b;
    }

    @Override
    public int getCount(){
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object){
        return view == (LinearLayout)object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewpager_item, container, false);

        ImageView imageView = (ImageView)view.findViewById(R.id.cardImage);
        imageView.setImageBitmap(imageList.get(position));
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){
        ((ViewPager) container).removeView((LinearLayout) object);
    }

    public void updateView(ArrayList<Bitmap> imageViews){
        imageList = imageViews;
        notifyDataSetChanged();
    }



}
