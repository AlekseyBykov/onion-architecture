package alekseybykov.portfolio.whitepappers.schedule;

import alekseybykov.portfolio.whitepappers.services.file.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Aleksey Bykov
 * @since 06.10.2019
 */
@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class ScheduledTasks {

    private final FilesService filesService;

    @Value("${jobs.enabled}")
    private Boolean isEnabled;

    @Scheduled(fixedDelayString = "${fileservice.move-delay}")
    public void moveAllFilesToTheCloud() {
        if (BooleanUtils.isTrue(isEnabled)) {
            Integer counter = filesService.moveAllFilesToTheCloud();
            if (counter > 0) {
                log.info("Moved files: {}", counter);
            }
        }
    }
}
