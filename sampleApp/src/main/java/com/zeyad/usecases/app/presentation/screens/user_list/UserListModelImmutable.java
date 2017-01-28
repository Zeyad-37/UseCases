package com.zeyad.usecases.app.presentation.screens.user_list;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.zeyad.usecases.app.utils.Utils;

import java.util.List;

/**
 * @author by ZIaDo on 1/28/17.
 */
@AutoValue
public abstract class UserListModelImmutable implements Parcelable {

    public static final String LOADING = "loading", ERROR = "error", NEXT = "next";

    public static UserListModelImmutable reduce(UserListModelImmutable previous, UserListModelImmutable changes) {
        if (previous == null)
            return changes;
        UserListModelImmutable.Builder onNextBuilder = UserListModelImmutable.builder();
        if ((previous.state().equals(LOADING) && changes.state().equals(NEXT)) ||
                (previous.state().equals(NEXT) && changes.state().equals(NEXT))) {
            onNextBuilder.setIsLoading(false)
                    .setError(null)
                    .setyScroll(previous.users().isEmpty() ? 0 : changes.yScroll() == 0 ? previous.yScroll() : changes.yScroll())
                    .setUsers(Utils.isNotEmpty(changes.users()) ? Utils.union(previous.users(), changes.users()) : previous.users())
                    .setState(NEXT);
        } else if (previous.state().equals(LOADING) && changes.state().equals(ERROR)) {
            onNextBuilder.setIsLoading(false)
                    .setError(changes.error())
                    .setyScroll(previous.users().isEmpty() ? 0 : changes.yScroll() == 0 ? previous.yScroll() : changes.yScroll())
                    .setUsers(Utils.isNotEmpty(changes.users()) ? Utils.union(previous.users(), changes.users()) : previous.users())
                    .setState(ERROR);
        } else if ((previous.state().equals(ERROR) && changes.state().equals(LOADING)) ||
                (previous.state().equals(NEXT) && changes.state().equals(LOADING))) {
            onNextBuilder.setError(null)
                    .setIsLoading(true)
                    .setState(LOADING)
                    .setyScroll(previous.users().isEmpty() ? 0 : changes.yScroll() == 0 ? previous.yScroll() : changes.yScroll())
                    .setUsers(Utils.isNotEmpty(changes.users()) ? Utils.union(previous.users(), changes.users()) : previous.users());
        } else
            throw new IllegalStateException("Don't know how to reduce the partial state " + changes.toString());
        return onNextBuilder.build();
    }

    public static UserListModelImmutable loading() {
        return UserListModelImmutable.builder()
                .setUsers(null)
                .setError(null)
                .setIsLoading(true)
                .setState(LOADING)
                .build();
    }

    public static UserListModelImmutable onNext(List<UserRealm> users) {
        return UserListModelImmutable.builder()
                .setUsers(users)
                .setError(null)
                .setIsLoading(false)
                .setState(NEXT)
                .build();
    }

    public static UserListModelImmutable error(Throwable error) {
        return UserListModelImmutable.builder()
                .setUsers(null)
                .setIsLoading(false)
                .setError(error)
                .setState(ERROR)
                .build();
    }

    static Builder builder() {
        return new AutoValue_UserListModelImmutable.Builder();
    }

    abstract List<UserRealm> users();

    abstract int yScroll();

    abstract boolean isLoading();

    @Nullable
    abstract Throwable error();

    abstract String state();

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setUsers(List<UserRealm> value);

        abstract Builder setyScroll(int value);

        abstract Builder setIsLoading(boolean value);

        abstract Builder setError(Throwable value);

        abstract Builder setState(String value);

        abstract UserListModelImmutable build();
    }
}
