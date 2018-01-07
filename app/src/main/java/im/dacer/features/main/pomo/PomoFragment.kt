package im.dacer.features.main.pomo

import android.os.Bundle
import android.view.View
import im.dacer.R
import im.dacer.features.base.BaseFragment
import javax.inject.Inject

/**
 * Created by Dacer on 02/01/2018.
 */
class PomoFragment : BaseFragment() , PomoMvpView {
    @Inject lateinit var pomoPresenter: PomoPresenter

    override fun layoutId() = R.layout.fragment_task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent().inject(this)
        pomoPresenter.attachView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}