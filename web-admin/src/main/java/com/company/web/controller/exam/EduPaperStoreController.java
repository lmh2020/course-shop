package com.company.web.controller.exam;

import java.util.List;

import com.company.common.validator.group.ValidGroup;
import com.company.exam.domain.EduQuestionStore;
import com.company.common.annotation.RepeatSubmit;
import com.company.common.utils.AssertUtil;
import com.company.exam.form.EduExamQuestionForPaperQueryForm;
import com.company.exam.vo.PaperQuestionStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.company.common.annotation.Log;
import com.company.common.core.controller.BaseController;
import com.company.common.core.domain.R;
import com.company.common.enums.BusinessType;
import com.company.exam.form.EduPaperStoreMergeForm;
import com.company.exam.form.EduPaperStoreQueryForm;
import com.company.exam.domain.EduPaperStore;
import com.company.exam.service.IEduPaperStoreService;
import com.company.common.utils.poi.ExcelUtil;
import com.company.common.core.page.TableDataInfo;

/**
 * 考核试卷基础信息Controller
 */
@RestController
@RequestMapping("edu/paper")
public class EduPaperStoreController extends BaseController {

    @Autowired
    private IEduPaperStoreService eduPaperStoreService;

    /**
     * 查询考核试卷基础信息列表
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:list')")
    @GetMapping("list")
        public TableDataInfo list(EduPaperStoreQueryForm queryForm) {
        startPage();
        List<EduPaperStore> list = eduPaperStoreService.selectEduPaperStoreList(queryForm);
        return super.getDataTable(list);
    }

    /**
     * 导出考核试卷基础信息列表
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:export')")
    @Log(title = "考核试卷基础信息", businessType = BusinessType.EXPORT)
    @GetMapping("export")
    public R export(EduPaperStoreQueryForm queryForm) {
        List<EduPaperStore> list = eduPaperStoreService.selectEduPaperStoreList(queryForm);
        ExcelUtil<EduPaperStore> util = new ExcelUtil<EduPaperStore>(EduPaperStore. class);
        return R.successWithData(util.exportExcel(list, "bbb"));
    }

    /**
     * 获取考核试卷基础信息详细信息
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:query')")
    @GetMapping(value = "{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(eduPaperStoreService.selectEduPaperStoreById(id));
    }

    /**
     * 新增考核试卷基础信息
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:add')")
    @Log(title = "考核试卷基础信息", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(ValidGroup.Insert.class) EduPaperStoreMergeForm mergeForm) {
        eduPaperStoreService.insertEduPaperStore(mergeForm);
        return R.success("添加成功");
    }

    /**
     * 修改考核试卷基础信息
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:edit')")
    @Log(title = "考核试卷基础信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(ValidGroup.Update.class) EduPaperStoreMergeForm mergeForm) {
        eduPaperStoreService.updateEduPaperStore(mergeForm);
        return R.success("修改成功");
    }

    /**
     * 删除考核试卷基础信息
     */
//    @PreAuthorize("@ss.hasPermi('edu:paper:remove')")
    @Log(title = "考核试卷基础信息", businessType = BusinessType.DELETE)
    @DeleteMapping("{ids}")
    public R remove(@PathVariable Long[] ids) {
        eduPaperStoreService.deleteEduPaperStoreByIds(ids);
        return R.success("删除成功");
    }

    /* ------------------------------------------- 下面是试卷的试题操作 -------------------------------------------- */

    /**
     * 从题库导入试题到试卷[单个、批量]
     */
    @Log(title = "批量从题库导入试题到试卷")
    @RepeatSubmit
    @PostMapping("importQuestion")
    public R importQuestion(@RequestParam Long paperId, @RequestParam Long[] storeQuestionIds) {
        eduPaperStoreService.importQuestion(paperId, storeQuestionIds);
        return R.success("导入成功");
    }

    /**
     * 查询某试卷的所有试题
     */
    @GetMapping("paperQuestionList")
    public R paperQuestionList(@Validated EduExamQuestionForPaperQueryForm queryForm) {
        return R.success("获取成功", eduPaperStoreService.selectPaperQuestionListWithGroup(queryForm));
    }

    /**
     * 分页从题库里查询【可导入某#试卷#】的所有试题（对题库结果进行了封装）
     */
    @GetMapping("storeQuestionListForPaper")
    public TableDataInfo storeQuestionListForPaper(@Validated EduExamQuestionForPaperQueryForm queryForm) {
        startPage();
        List<EduQuestionStore> eduQuestionStores = eduPaperStoreService.selectStoreQuestionForPaper(queryForm);
        return super.getDataTable(eduQuestionStores);
    }


    /**
     * 从试卷移除
     */
    @DeleteMapping("paperQuestion")
    public R removePaperQuestion(@RequestParam Long paperId, @RequestParam Long storeQuestionId) {
        eduPaperStoreService.removePaperQuestion(paperId, storeQuestionId);
        return R.success("添加成功移除");
    }

    /**
     * 调换试卷试题的顺序
     */
    @RepeatSubmit
    @PostMapping("changePaperQuestionSort")
    public R changePaperQuestionSort(@RequestParam Long paperId, @RequestParam Long fromId, @RequestParam Long toId, @RequestParam String opt) {
        eduPaperStoreService.changePaperQuestionSort(opt, paperId, fromId, toId);
        return R.success("操作成功");
    }

    /**
     * 试卷统计
     */
    @GetMapping("statisticsPaperInfo/{paperId}/{searchMode}")
    public R changePaperQuestionSort(@PathVariable Long paperId, @PathVariable String[] searchMode) {
        AssertUtil.isEmpty(searchMode, "查询类型不能为空");
        PaperQuestionStatisticsVO statisticsVO = eduPaperStoreService.statisticsPaperInfo(paperId, searchMode);
        return R.successWithData(statisticsVO);
    }

}
