package com.zeyad.usecases.app.presentation.screens.user_list.view_holders;

import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zeyad.usecases.app.R;
import com.zeyad.usecases.app.components.adapter.GenericRecyclerViewAdapter;
import com.zeyad.usecases.app.presentation.models.UserRealm;
import com.zeyad.usecases.data.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author zeyad on 12/1/16.
 */
public class UserViewHolder extends GenericRecyclerViewAdapter.ViewHolder {
    @BindView(R.id.title)
    TextView textViewTitle;
    @BindView(R.id.avatar)
    SimpleDraweeView avatar;
    @BindView(R.id.rl_row_user)
    RelativeLayout rowUser;

    public UserViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
        if (data != null) {
//            UserModel userModel = (UserModel) data;
            UserRealm userModel = (UserRealm) data;
//        avatar.setImageURI(Uri.parse(userModel.getAvatarUrl()));
            if (Utils.isNotEmpty(userModel.getAvatarUrl()))
                avatar.setController(Fresco.newDraweeControllerBuilder()
                        .setUri(Uri.parse(userModel.getAvatarUrl()))
                        .setTapToRetryEnabled(true)
                        .setOldController(avatar.getController())
//                .setControllerListener(new BaseControllerListener())
                        .build());
            if (Utils.isNotEmpty(userModel.getLogin()))
                textViewTitle.setText(userModel.getLogin());
        }
    }
}
