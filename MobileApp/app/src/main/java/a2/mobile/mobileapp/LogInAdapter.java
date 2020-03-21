package a2.mobile.mobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogInAdapter extends RecyclerView.Adapter<LogInAdapter.ViewHolder> {
    private final Context context;
    private final List<AuthenticationOption> authenticationOptions;

    private LayoutInflater inflater;

    LogInAdapter(Context context, List<AuthenticationOption> authenticationOptions) {
        this.context = context;
        this.authenticationOptions = authenticationOptions;

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.authentication_option, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuthenticationOption authenticationOption = authenticationOptions.get(position);

        holder.icon.setImageResource(authenticationOption.icon);
        holder.title.setText(authenticationOption.title);
    }

    @Override
    public int getItemCount() {
        return authenticationOptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
