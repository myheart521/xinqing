package com.hpu.xinqing.service.serviceImpl;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hpu.xinqing.mapper.NewKnowledgeCollectionMapper;
import com.hpu.xinqing.mapper.NewKnowledgeLikeMapper;
import com.hpu.xinqing.mapper.NewKnowledgeMapper;
import com.hpu.xinqing.service.NewKnowledgeService;
import com.hpu.xinqingcommon.utils.StpUtils;
import com.hpu.xinqingpojo.DTO.BasePageDTO;
import com.hpu.xinqingpojo.DTO.PageDTO;
import com.hpu.xinqingpojo.VO.NewKnowledgeInfoVo;
import com.hpu.xinqingpojo.VO.NewKnowledgeListVO;
import com.hpu.xinqingpojo.entity.NewKnowledge;
import com.hpu.xinqingpojo.entity.NewKnowledgeCollection;
import com.hpu.xinqingpojo.entity.NewKnowledgeLike;
import org.jetbrains.annotations.NotNull;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class NewKnowledgeServiceImpl extends ServiceImpl<NewKnowledgeMapper, NewKnowledge> implements NewKnowledgeService {

    @Autowired
    private NewKnowledgeLikeMapper newKnowledgeLikeMapper;
    @Autowired
    private NewKnowledgeCollectionMapper newKnowledgeCollectionMapper;
    @Resource
    NewKnowledgeMapper newKnowledgeMapper;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public List<NewKnowledgeListVO> pageQuery(PageDTO pageDTO) {
        Page<NewKnowledge> newKnowledgePage = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());
        OrderItem create_time = new OrderItem().setAsc(false).setColumn("create_time");
        newKnowledgePage.addOrder(create_time);
        List<String> list = List.of(new String[]{"id","title","descriptions","cover_image","tags","color","view_count","like_count","collection_count"});
        //去数据库查除是否点赞以外的数据
        Page<NewKnowledge> page = page(newKnowledgePage,new QueryWrapper<NewKnowledge>().select(list));

        Long userId = StpUtils.userId();
        //当前用户对哪些文章点赞了
        List<NewKnowledgeLike> isLikeList = newKnowledgeLikeMapper.selectList(
                new QueryWrapper<NewKnowledgeLike>().eq("user_id", userId)
        );
        //当前用户对哪些文章收藏了
        List<NewKnowledgeCollection> isCollectionList = newKnowledgeCollectionMapper.selectList(
                new QueryWrapper<NewKnowledgeCollection>().eq("user_id", userId)
        );
        // 将用户点赞的文章 ID 存入集合，便于快速查找
        Set<Long> likedKnowledgeIds = isLikeList.stream()
                .map(NewKnowledgeLike::getNewKnowledgeId)
                .collect(Collectors.toSet());
        // 将用户收藏的文章 ID 存入集合，便于快速查找
        Set<Long> collectKnowledgeIds = isCollectionList.stream()
                .map(NewKnowledgeCollection::getNewKnowledgeId)
                .collect(Collectors.toSet());
        List<NewKnowledge> listRecord = page.getRecords();
        return getNewKnowledgeListVOS(likedKnowledgeIds, collectKnowledgeIds, listRecord);
    }

    @NotNull
    private static List<NewKnowledgeListVO> getNewKnowledgeListVOS(Set<Long> likedKnowledgeIds, Set<Long> collectKnowledgeIds, List<NewKnowledge> listRecord) {
        List<NewKnowledgeListVO> records = new ArrayList<>();
        for (NewKnowledge newKnowledge : listRecord) {
            String[] tags = newKnowledge.getTags().split(","); // 将 tags 字符串分割为数组
            NewKnowledgeListVO vo = new NewKnowledgeListVO(
                    newKnowledge.getId(),
                    newKnowledge.getTitle(),
                    newKnowledge.getDescriptions(),
                    newKnowledge.getCoverImage(),
                    tags,
                    newKnowledge.getColor(),
                    newKnowledge.getViewCount(),
                    newKnowledge.getLikeCount(),
                    newKnowledge.getCollectionCount(),
                    //是否包含有这个id
                    likedKnowledgeIds.contains(newKnowledge.getId()),
                    collectKnowledgeIds.contains(newKnowledge.getId())
            );
            records.add(vo); // 将构建好的 NewKnowledgeListVO 添加到结果列表中
        }
        return records;
    }

    @Override
    @Transactional
    public NewKnowledgeInfoVo view(Integer id) {
        //使用事务，避免查出来发现有，更新的时候发现没有了（幻读）
        NewKnowledge newKnowledge= getById(id);
        NewKnowledgeInfoVo newKnowledgeInfoVo = new NewKnowledgeInfoVo(newKnowledge.getTitle(),newKnowledge.getCreateTime().toLocalDate(),newKnowledge.getContent());
        newKnowledge.setViewCount(newKnowledge.getViewCount()+1);
        updateById(newKnowledge);
        return newKnowledgeInfoVo;
    }

    @Override
    public void like(Integer id) {
        starOrCollection(id,"like");
    }

    @Override
    public void collection(Integer id) {
        starOrCollection(id,"collection");
    }
    @Transactional
    public void starOrCollection(Integer id, String action) {
        UpdateWrapper<NewKnowledge> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id); // 条件：指定文章 ID


        Long userId = StpUtils.userId();

        if ("like".equals(action)) {
            // 点赞逻辑
            QueryWrapper<NewKnowledgeLike> isLike = new QueryWrapper<NewKnowledgeLike>()
                    .eq("user_id", userId)
                    .eq("new_knowledge_id", id);
            NewKnowledgeLike newKnowledgeLike = newKnowledgeLikeMapper.selectOne(isLike);

            if (ObjectUtil.isNull(newKnowledgeLike)) {
                // 点赞
                updateWrapper.setSql("like_count = like_count + 1");
                newKnowledgeLike = new NewKnowledgeLike();
                newKnowledgeLike.setUserId(userId);
                newKnowledgeLike.setNewKnowledgeId(Long.valueOf(id));
                newKnowledgeLikeMapper.insert(newKnowledgeLike);
            } else {
                // 取消点赞
                updateWrapper.setSql("like_count = like_count - 1");
                newKnowledgeLikeMapper.deleteById(newKnowledgeLike.getId());
            }
        } else if ("collection".equals(action)) {
            // 收藏逻辑
            QueryWrapper<NewKnowledgeCollection> isCollection = new QueryWrapper<NewKnowledgeCollection>()
                    .eq("user_id", userId)
                    .eq("new_knowledge_id", id);
            NewKnowledgeCollection newKnowledgeCollection = newKnowledgeCollectionMapper.selectOne(isCollection);

            if (ObjectUtil.isNull(newKnowledgeCollection)) {
                // 收藏
                updateWrapper.setSql("collection_count = collection_count + 1");
                newKnowledgeCollection = new NewKnowledgeCollection();
                newKnowledgeCollection.setUserId(userId);
                newKnowledgeCollection.setNewKnowledgeId(Long.valueOf(id));
                newKnowledgeCollectionMapper.insert(newKnowledgeCollection);
            } else {
                // 取消收藏
                updateWrapper.setSql("collection_count = collection_count - 1");
                newKnowledgeCollectionMapper.deleteById(newKnowledgeCollection.getId());
            }
        }

        // 调用 update 方法执行更新操作
        update(updateWrapper);
    }

    @Override
    public List<NewKnowledgeListVO> pageCollectionQuery(PageDTO pageDTO) {
        Page<NewKnowledgeCollection> newKnowledgePage = new Page<>(pageDTO.getPageNo(), pageDTO.getPageSize());

        OrderItem create_time = new OrderItem().setAsc(false).setColumn("create_time");
        newKnowledgePage.addOrder(create_time);
        //todo 便于测试，固定id为2
        Long userId = StpUtils.userId();
        //当前用户对哪些文章点赞了
        List<NewKnowledgeLike> isLikeList = newKnowledgeLikeMapper.selectList(
                new QueryWrapper<NewKnowledgeLike>().eq("user_id", userId)
        );
        //当前用户对哪些文章收藏了
        List<NewKnowledgeCollection> isCollectionList = newKnowledgeCollectionMapper.selectPage(newKnowledgePage,
                new QueryWrapper<NewKnowledgeCollection>().eq("user_id", userId)
        ).getRecords();
        // 将用户点赞的文章 ID 存入集合，便于快速查找
        Set<Long> likedKnowledgeIds = isLikeList.stream()
                .map(NewKnowledgeLike::getNewKnowledgeId)
                .collect(Collectors.toSet());

        // 将用户收藏的文章 ID 存入集合，便于快速查找
        Set<Long> collectKnowledgeIds = isCollectionList.stream()
                .map(NewKnowledgeCollection::getNewKnowledgeId)
                .collect(Collectors.toSet());

        List<Long> collectKnowledgeIdLists = isCollectionList.stream()
                .map(NewKnowledgeCollection::getNewKnowledgeId)
                .collect(Collectors.toList());
        String ids = StrUtil.join(",", collectKnowledgeIdLists);
        QueryWrapper<NewKnowledge> newKnowledgeQueryWrapper = new QueryWrapper<>();
        newKnowledgeQueryWrapper.in("id",collectKnowledgeIds).last("order by field(id," + ids + ")");
        List<NewKnowledge> list = this.list(newKnowledgeQueryWrapper);
        List<NewKnowledgeListVO> newKnowledgeListVOS = getNewKnowledgeListVOS(likedKnowledgeIds, collectKnowledgeIds, list);

        return newKnowledgeListVOS;

    }

    public List<NewKnowledge> getByUserId(Long userId, BasePageDTO basePageDTO){
        Page<NewKnowledge> page = new Page<>(basePageDTO.getCurrent(),basePageDTO.getPageSize());
        return lambdaQuery()
                .eq(NewKnowledge::getCreateId,userId)
                .page(page)
                .getRecords();
    }

    @Override
    public List<Map> getMapByUserId(Long userId, BasePageDTO basePageDTO) {
        return getByUserId(userId,basePageDTO)
                .stream()
                .map(newKnowledge -> objectMapper.convertValue(newKnowledge,Map.class))
                .toList();
    }

    @Override
    public List<NewKnowledge> getAll() {
        return lambdaQuery().list();
    }

    @Override
    public List<NewKnowledge> getListByIds(List<Long> ids) {
        return newKnowledgeMapper.selectBatchIds(ids);
    }

    @Override
    public List<Long> getAllIds() {
        return newKnowledgeMapper.selectObjs(lambdaQuery().select(NewKnowledge::getId))
                .stream().map(o -> (Long) o).toList();
    }
}
