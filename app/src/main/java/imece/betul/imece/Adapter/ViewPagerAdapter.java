package imece.betul.imece.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import imece.betul.imece.Fragments.PostDetailFragment;
import imece.betul.imece.R;

import static android.content.Context.MODE_PRIVATE;

public class ViewPagerAdapter extends android.support.v4.view.PagerAdapter {
    private Context  context;
    List<String>  imageurl;
    String id;
    public ViewPagerAdapter(Context context, List<String> imageurl,String id){
        this.id=id;
        this.context = context;
        this.imageurl=imageurl;
    }
    @Override
    public int getCount() {
        return imageurl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== object ;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView =new ImageView(context);
        Picasso.get()
                .load(imageurl.get(position))
                .fit().centerCrop().into(imageView);
        container.addView(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("postid",id );
                        editor.apply();

                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new PostDetailFragment()).commit();
                    }
                });

        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
