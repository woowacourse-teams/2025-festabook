package com.daedan.festabook.logging

import androidx.viewbinding.ViewBinding
import com.daedan.festabook.FestaBookApp

val ViewBinding.logger get() = (root.context.applicationContext as FestaBookApp).festaBookGraph.defaultFirebaseLogger
