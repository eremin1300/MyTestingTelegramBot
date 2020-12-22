package vik.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import vik.test.models.DailyDomains;


@EnableJpaRepositories
public interface DailyDomainsRepo extends JpaRepository<DailyDomains, Long> {

}
