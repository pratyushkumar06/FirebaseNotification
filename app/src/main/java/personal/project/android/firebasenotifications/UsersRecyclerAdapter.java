package personal.project.android.firebasenotifications;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder> {
    private List<Users>  List;
    Context ctx,ctx2;

    public UsersRecyclerAdapter(List<Users> List,Context ctx){
        this.List =List;
        this.ctx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        ctx2=parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setDetails(ctx,List.get(position).getName(),List.get(position).getUrl());
        final String user_id=List.get(position).UserId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ctx2,SendActivity.class);
                intent.putExtra("UID",user_id);
                intent.putExtra("Name",List.get(position).getName());
                ctx2.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;

        }

        public void setDetails(Context context,String username, String userimage){
            TextView uname=mView.findViewById(R.id.naaame);
            ImageView imageView=mView.findViewById(R.id.imageView);

            uname.setText(username);
            Glide.with(context).load(userimage).apply(RequestOptions.circleCropTransform()).into(imageView);
        }
    }

}
