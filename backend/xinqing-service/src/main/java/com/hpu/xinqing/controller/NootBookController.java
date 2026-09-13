package com.hpu.xinqing.controller;


import com.hpu.xinqing.service.INootBookService;
import com.hpu.xinqingpojo.DTO.NootBookDTO;
import com.hpu.xinqingpojo.entity.NootBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.hpu.xinqingcommon.result.PageResult;
import com.hpu.xinqingcommon.result.Result;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @since 2024-09-11
 */
@RestController
@RequestMapping("/nootBook")
@RequiredArgsConstructor
@Slf4j
public class NootBookController {

    private final INootBookService nootBookService;

    /**
     * 查询全部标题
     * @param nootBookDTO
     * @return
     */
    @GetMapping("/list")
    public PageResult list(@RequestBody NootBookDTO nootBookDTO){
        log.info("查询笔记数据,{}",nootBookDTO);
        PageResult pageResult=nootBookService.listByUserId(nootBookDTO);
        return pageResult;
    }

    @GetMapping("/{id}")
    public Result queryById(@PathVariable Long id){
        log.info("查询笔记信息：{}",id);
        NootBook nootBook = nootBookService.getById(id);
        return Result.success(nootBook);
    }

    @PostMapping("/add")
    public Result add(@RequestBody NootBook nootBook){
        log.info("新增笔记,{}",nootBook);
        nootBookService.save(nootBook);
        return Result.success();
    }

    @DeleteMapping
    public Result delete(@RequestParam Long id){
        log.info("删除笔记id：{}",id);
        nootBookService.removeById(id);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody NootBook nootBook){
        log.info("更新笔记：{}",nootBook);
        nootBookService.updateById(nootBook);
        return Result.success();
    }







}
