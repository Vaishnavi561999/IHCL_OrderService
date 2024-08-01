package com.ihcl.order.model.exception

class QCInternalServerException (
    override val message: String?
):Exception()

class QCTimeOutException(override val message: String?):Exception()
class QCOrderNotAvailableException(override val message: String?):Exception()
class OrderInternalServerException (
    override val message: String?
):Exception()