package de.dojodev.royalrangermanager.controller

class HomeController : SubController() {
    override fun initControls() {
        this.cmdNew?.isVisible = true
        this.cmdEdit?.isVisible = true
        this.cmdDelete?.isVisible = true
        this.cmdCancel?.isVisible = true
        this.cmdSave?.isVisible = true
    }

    override fun init() {

    }
}