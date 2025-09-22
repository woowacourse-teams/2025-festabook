package com.daedan.festabook.logging

import androidx.viewbinding.ViewBinding

val ViewBinding.logger get() = DefaultFirebaseLogger.getInstance(root.context)
