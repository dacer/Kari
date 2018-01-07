package im.dacer.common.injection.component

import dagger.Component
import im.dacer.common.injection.module.ApplicationTestModule
import im.dacer.injection.component.AppComponent
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationTestModule::class))
interface TestComponent : AppComponent