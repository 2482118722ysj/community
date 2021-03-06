package com.company.community.mapper;

import com.company.community.models.Publish;

import java.util.List;

public interface PublishMapperCustom {

    List<Publish> selectPublishList(String search);

    List<Publish> selectPublistByCreatorId(Integer createId);

    void updateQuestion(Publish publish);

    void incView(Integer id);

    void inComment(Integer id);

    List<Publish> selectPublishByTags(Publish publish);
}
