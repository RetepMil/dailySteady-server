package retepmil.personal.dailysteady

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class DailysteadyApplication

fun main(args: Array<String>) {
	runApplication<DailysteadyApplication>(*args)
}
