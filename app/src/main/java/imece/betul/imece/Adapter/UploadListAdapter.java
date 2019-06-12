package imece.betul.imece.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.security.AccessControlContext;
import java.util.ArrayList;

import imece.betul.imece.R;


public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder>{

    public  ArrayList<Uri> fileNameList;




    public UploadListAdapter( ArrayList<Uri> fileNameList) {
        this.fileNameList = fileNameList;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            Uri uri =fileNameList.get(position);
            holder.fileDoneView.setImageURI(uri);


    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public ImageView fileDoneView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;


            fileDoneView = (ImageView) mView.findViewById(R.id.upload_icon);


        }

    }

}
