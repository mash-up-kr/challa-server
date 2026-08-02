package com.challa.core.upload

interface IssuePhotoUploadUseCase {
    fun issue(command: IssuePhotoUploadCommand): IssuePhotoUploadResult
}
