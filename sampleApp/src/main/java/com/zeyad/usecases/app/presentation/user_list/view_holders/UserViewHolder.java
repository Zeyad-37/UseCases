package com.zeyad.usecases.app.presentation.user_list.view_holders;

import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.models.UserModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 12/1/16.
 */
public class UserViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.avatar)
    DraweeView avatar;
    @BindView(R.id.rl_row_user)
    RelativeLayout rowUser;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
        UserModel userModel = (UserModel) data;
//        avatar.setImageURI(Uri.parse(userModel.getAvatarUrl()));
        avatar.setController(Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(userModel.getAvatarUrl()))
                .setTapToRetryEnabled(true)
                .setOldController(avatar.getController())
//                .setControllerListener(new BaseControllerListener())
                .build());
        textViewTitle.setText(userModel.getLogin());
    }
}
