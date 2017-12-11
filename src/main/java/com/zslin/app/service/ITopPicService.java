package com.zslin.app.service;

import com.zslin.app.model.TopPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ITopPicService extends JpaRepository<TopPic, Integer>, JpaSpecificationExecutor<TopPic> {
}
