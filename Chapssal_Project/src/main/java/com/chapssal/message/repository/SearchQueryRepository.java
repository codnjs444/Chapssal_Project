package com.chapssal.message.repository;

import com.chapssal.message.model.SearchQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface SearchQueryRepository extends JpaRepository<SearchQuery, Long> {
    @Query("SELECT q.query, COUNT(q) as count FROM SearchQuery q GROUP BY q.query ORDER BY count DESC")
    List<Object[]> findTopSearchQueries();


    @Query("SELECT q.query, COUNT(q) as count FROM SearchQuery q WHERE q.searchDate > :since GROUP BY q.query ORDER BY count DESC")
    List<Object[]> findTopSearchQueriesSince(@Param("since") Date since);

}
