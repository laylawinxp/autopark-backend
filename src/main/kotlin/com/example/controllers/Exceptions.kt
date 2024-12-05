package com.example.controllers

class BadRequestException(message: String?) : Exception(message)
class NotFoundException(message: String?) : Exception(message)
class TriggerException(message: String, cause: Throwable?) : Exception(message, cause)
