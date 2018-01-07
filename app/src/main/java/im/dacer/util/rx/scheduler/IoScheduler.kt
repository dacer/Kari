package im.dacer.util.rx.scheduler

import io.reactivex.schedulers.Schedulers

/**
 * Created by lam on 2/6/17.
 */

class IoScheduler<T> constructor() : BaseScheduler<T>(Schedulers.io(), Schedulers.io())
