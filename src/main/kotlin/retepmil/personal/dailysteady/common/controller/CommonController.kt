package retepmil.personal.dailysteady.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CommonController {

    @GetMapping("health")
    fun healthCheck(): String = "OK"

}