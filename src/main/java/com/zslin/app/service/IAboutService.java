package com.zslin.app.service;

import com.zslin.app.model.About;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAboutService extends JpaRepository<About, Integer> {

}
