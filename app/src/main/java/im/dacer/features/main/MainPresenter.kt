package im.dacer.features.main

import im.dacer.data.DataManager
import im.dacer.features.base.BasePresenter
import im.dacer.injection.ConfigPersistent
import javax.inject.Inject


@ConfigPersistent
class MainPresenter @Inject
constructor(private val dataManager: DataManager) : BasePresenter<MainMvpView>() {


}