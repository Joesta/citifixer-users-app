package com.dso30bt.project2019.potapp.interfaces;

import com.dso30bt.project2019.potapp.models.User;

/**
 * Created by Joesta on 2019/08/27.
 */
public interface IFirebase {

    interface DocumentFieldCallback {
        void onFieldUpdate(int updatedFieldId);
    }

    interface UserCallback {
        void onFetchedUser(User user);
    }
}
