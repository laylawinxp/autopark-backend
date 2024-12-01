package com.example.controllers

class BadRequestException(message: String?) : Exception(message)
class NotFoundException(message: String?) : Exception(message)
class TriggerConflictException(message: String?) : Exception(message)