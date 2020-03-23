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
    private final String TAG = "Log In Adapter";
    private final String KEY_NAME = "Android Key";

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
        View rootView = inflater.inflate(R.layout.authentication_option, parent, false);

        rootView.setOnClickListener(view -> {

            // Change colors of the title and the icon.
            TextView title = view.findViewById(R.id.title);
            ImageView icon = view.findViewById(R.id.icon);

            title.setTextColor(view.getResources().getColor(R.color.blue_darker));
            icon.setColorFilter(view.getResources().getColor(R.color.blue));

            // Get the background drawable shape to change its background and stroke colors.
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

            Object cardTag = card.getTag();
            if (cardTag.equals(R.string.face_id_id)) {
                // onFaceIdOptionClick();
            } else if (cardTag.equals(R.string.fingerprint_id)) {
                onFingerprintOptionClick();
            } else if (cardTag.equals(R.string.passcode_id)) {
                onPasscodeOptionClick();
            }
        });

        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuthenticationOption authenticationOption = authenticationOptions.get(position);

        holder.card.setTag(authenticationOption.id);
        holder.icon.setImageResource(authenticationOption.icon);
        holder.title.setText(authenticationOption.title);

        activityToStart = authenticationOption.activityToStart;
    }

    @Override
    public int getItemCount() {
        return authenticationOptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView title;
        ImageView icon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.option);
            title = itemView.findViewById(R.id.title);
            icon = itemView.findViewById(R.id.icon);
        }
    }

    /**
     * Begin fingerprint authentication.
     */
    private void onFingerprintOptionClick() {
        FingerprintHandler fingerprintHandler = new FingerprintHandler(context, activityToStart);
        fingerprintHandler.onFingerprintOptionClick();
    }

    /**
     * Reveal the hidden passcode field and hide the authentication options list.
     */
    private void onPasscodeOptionClick() {
        PasscodeHandler passcodeHandler = new PasscodeHandler(context, activityToStart);
        passcodeHandler.onPasscodeOptionClick();
    }
}
