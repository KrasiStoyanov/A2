package a2.mobile.mobileapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogInAdapter extends RecyclerView.Adapter<LogInAdapter.ViewHolder> {
    private final Context context;
    private final List<AuthenticationOption> authenticationOptions;
    private Intent activityToStart;

    private LayoutInflater inflater;

    LogInAdapter(Context context, List<AuthenticationOption> authenticationOptions) {

        this.context = context;
        this.authenticationOptions = authenticationOptions;
        this.activityToStart = new Intent();

        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.authentication_option, parent, false);
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TextView title = view.findViewById(R.id.title);
                ImageView icon = view.findViewById(R.id.icon);

                title.setTextColor(view.getResources().getColor(R.color.blue_darker));
                icon.setColorFilter(view.getResources().getColor(R.color.blue));

                GradientDrawable buttonRadius = (GradientDrawable) view.getResources()
                        .getDrawable(R.drawable.button_radius);

                buttonRadius.setColor(view.getResources().getColor(R.color.dust));
                buttonRadius.setStroke(
                        1,
                        view.getResources().getColor(R.color.blue),
                        0,
                        0
                );

                CardView card = view.findViewById(R.id.option);
                card.setBackground(buttonRadius);

                context.startActivity(activityToStart);
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuthenticationOption authenticationOption = authenticationOptions.get(position);

        holder.icon.setImageResource(authenticationOption.icon);
        holder.title.setText(authenticationOption.title);

        activityToStart = authenticationOption.activityToStart;
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
