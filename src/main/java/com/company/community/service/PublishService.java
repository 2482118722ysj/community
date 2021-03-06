package com.company.community.service;

import com.company.community.dto.PageDTO;
import com.company.community.dto.PublishDTO;
import com.company.community.exception.ExceptionEnum;
import com.company.community.exception.MyException;
import com.company.community.mapper.PublishMapper;
import com.company.community.mapper.PublishMapperCustom;
import com.company.community.mapper.UserMapper;
import com.company.community.mapper.UserMapperCustom;
import com.company.community.models.Publish;
import com.company.community.models.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PublishService {

    private final static Integer navigatePages = 3;

    @Autowired
    private PublishMapperCustom publishMapperCustom;

    @Autowired
    private UserMapperCustom userMapperCustom;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PublishMapper publishMapper;

    @Autowired
    private PageDTO pageDTO;

    public List<PublishDTO> selectPublishList(Integer pageNum, Integer pageSize,String search) {
        List<PublishDTO> dtoArrrayList = new ArrayList<PublishDTO>();
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        //搜索问题功能
        if(search!=null){
            String[] split = search.split(" ");
            search = Arrays.stream(split).collect(Collectors.joining("|"));
        }
        List<Publish> publishList = publishMapperCustom.selectPublishList(search);
        for (Publish publish : publishList) {
            PublishDTO publishDTO = new PublishDTO();
            //将publishList中的每个属性copy到publishDTO中
            BeanUtils.copyProperties(publish, publishDTO);
            //根据creator查找到对应的user并绑定
            User user = userMapperCustom.selectByCreattorId(publish.getCreator());
            publishDTO.setUser(user);
            dtoArrrayList.add(publishDTO);
        }
        //每次连续查询n页
        PageInfo<Publish> pageInfo = new PageInfo<>(publishList, navigatePages);
        pageDTO.setPageInfo(pageInfo);
        return dtoArrrayList;
    }


    public List<PublishDTO> selectPublistByCreatorId(Integer createId, Integer pageNum, Integer pageSize) {
        List<PublishDTO> publishDTOS=new ArrayList<>();
        PageHelper.startPage(pageNum, pageSize);
        List<Publish> publishList = publishMapperCustom.selectPublistByCreatorId(createId);
        User user = userMapper.selectByPrimaryKey(createId);
        for (Publish publish : publishList) {
            PublishDTO publishDTO = new PublishDTO();
            BeanUtils.copyProperties(publish,publishDTO);
            publishDTO.setUser(user);
            publishDTOS.add(publishDTO);
        }
        //连续展示的页面
        PageInfo<Publish> profilePageInfo = new PageInfo<>(publishList, navigatePages);
        pageDTO.setProfilePageInfo(profilePageInfo);
        return publishDTOS;
    }


    public PublishDTO selectPublishById(Integer id) {
        Publish publish = publishMapper.selectByPrimaryKey(id);
        if (publish == null) {
            throw new MyException(ExceptionEnum.QEUSTION);
        }
        PublishDTO publishDTO = new PublishDTO();
        BeanUtils.copyProperties(publish, publishDTO);
        User user = userMapperCustom.selectByCreattorId(publish.getCreator());
        publishDTO.setUser(user);
        return publishDTO;
    }

    public void updateOrinsertQuestion(Publish publish, Integer id) {
        if (id != null) {
            //更新
            publish.setGmtModified(System.currentTimeMillis());
            publishMapperCustom.updateQuestion(publish);
        } else {
            //插入
            publish.setGmtCreate(System.currentTimeMillis());
            publish.setGmtModified(publish.getGmtCreate());
            publishMapper.insertSelective(publish);
        }
    }

    public void incView(Integer id) {
        publishMapperCustom.incView(id);
    }

    public List<Publish> selectPublishByTags(PublishDTO publishDTO) {
        List<Publish> publishList = null;
        if (publishDTO != null) {
            Publish publish = new Publish();
            publish.setId(publishDTO.getId());
            String[] split = publishDTO.getTag().split(",");
            String tags = Arrays.stream(split).collect(Collectors.joining("|"));
            publish.setTag(tags);
            publishList = publishMapperCustom.selectPublishByTags(publish);
        } else {
            throw new MyException(ExceptionEnum.QEUSTION);
        }
        return publishList;
    }

    public String fileupload(MultipartFile file){
        if(StringUtils.isEmpty(file)){
            return null;
        }
        //拿到文件名
        String fileName=file.getOriginalFilename();
        //后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //上传的路径
        String filepath="D:\\idea3\\images\\";
        fileName= UUID.randomUUID().toString().substring(0,12)+suffixName;
        File dest = new File(filepath+fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            //方法将上传文件写到服务器上指定的文件
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
