package com.wafflestudio.snutt2.lib.rx

class OutOfStateException : IllegalStateException {
    constructor() : super()
    constructor(s: String) : super(s)
    constructor(s: String, t: Throwable) : super(s, t)
}
