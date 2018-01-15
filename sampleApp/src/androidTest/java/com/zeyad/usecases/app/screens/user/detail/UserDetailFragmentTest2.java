package com.zeyad.usecases.app.screens.user.detail;

import com.android21buttons.fragmenttestrule.FragmentTestRule;
import com.zeyad.usecases.app.screens.user.list.User;

import org.junit.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by ZIaDo on 8/1/17.
 */
public class UserDetailFragmentTest2 {
    @Rule
    public FragmentTestRule<?, UserDetailFragment> fragmentTestRule =
            FragmentTestRule.create(UserDetailFragment.class);
    private User user;
    private List<Repository> repositories;

    private User mockUser() {
        user = new User();
        user.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
        user.setId(5938141);
        user.setLogin("Zeyad-37");
        return user;
    }

    private List<Repository> mockRepos() {
        repositories = new ArrayList<>();
        Repository repository = new Repository();
        repository.setId(1);
        repository.setName("Repo");
        repository.setOwner(user);
        return repositories;
    }
}
