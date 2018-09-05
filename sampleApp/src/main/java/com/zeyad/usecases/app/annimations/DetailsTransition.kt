package com.zeyad.usecases.app.annimations

import android.os.Build
import android.support.annotation.RequiresApi
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet

/**
 * @author ZIaDo on 3/29/16.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
class DetailsTransition @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
constructor() : TransitionSet() {

    init {
        ordering = TransitionSet.ORDERING_TOGETHER
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
    }
}

