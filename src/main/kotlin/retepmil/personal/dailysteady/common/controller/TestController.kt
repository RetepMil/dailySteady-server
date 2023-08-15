package retepmil.personal.dailysteady.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("health")
class TestController {

    @GetMapping
    fun healthCheck(): String {
        return "OK"
    }

}