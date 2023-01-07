package com.company.web.controller.exam;

import java.util.List;

import com.company.common.validator.group.ValidGroup;
import com.company.exam.domain.EduExam;
import com.company.exam.form.EduQuestionStoreMergeForm;
import com.company.exam.form.EduQuestionStoreQueryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.company.common.annotation.Log;
import com.company.common.core.controller.BaseController;
import com.company.common.core.domain.R;
import com.company.common.enums.BusinessType;
import com.company.exam.domain.EduQuestionStore;
import com.company.exam.service.IEduQuestionStoreService;
import com.company.common.utils.poi.ExcelUtil;
import com.company.common.core.page.TableDataInfo;

/**
 * 考试试卷题目题库Controller
 */
@RestController
@RequestMapping("edu/question/store")
public class EduQuestionStoreController extends BaseController {

    @Autowired
    private IEduQuestionStoreService eduQuestionStoreService;

    /**
     * 查询考试试卷题目列表
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:list')")
    @GetMapping("list")
    public TableDataInfo list(EduQuestionStoreQueryForm queryForm) {
        startPage();
        List<EduQuestionStore> list = eduQuestionStoreService.selectEduQuestionStoreList(queryForm);
        return super.getDataTable(list);
    }

    /**
     * 导出考试试卷题目列表
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:export')")
    @Log(title = "考试试卷题目", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public R export(EduQuestionStoreQueryForm queryForm) {
        List<EduQuestionStore> list = eduQuestionStoreService.selectEduQuestionStoreList(queryForm);
        ExcelUtil<EduQuestionStore> util = new ExcelUtil<>(EduQuestionStore.class);
        return R.successWithData(util.exportExcel(list, "question"));
    }

    /**
     * 获取考试试卷题目详细信息
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:query')")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(eduQuestionStoreService.selectEduQuestionStoreById(id));
    }

    /**
     * 新增考试试卷题目
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:add')")
    @Log(title = "考试试卷题目", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(ValidGroup.Insert.class) EduQuestionStoreMergeForm createForm) {
        eduQuestionStoreService.insertEduQuestionStore(createForm);
        return R.success("添加成功");
    }

    /**
     * 修改考试试卷题目
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:edit')")
    @Log(title = "考试试卷题目", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(ValidGroup.Update.class) EduQuestionStoreMergeForm updateForm) {
        eduQuestionStoreService.updateEduQuestionStore(updateForm);
        return R.success("修改成功");
    }

    /**
     * 删除考试试卷题目
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:store:remove')")
    @Log(title = "考试试卷题目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@PathVariable Long[] ids) {eduQuestionStoreService.deleteEduQuestionStoreByIds(ids);
        eduQuestionStoreService.deleteEduQuestionStoreByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 获取所有引用了该试题的试卷信息
     */
    @GetMapping("referredExams/{questionId}")
    public R getExamQuestionReferredExams(@PathVariable String questionId) {
        List<EduExam> examQuestionReferredPaper = eduQuestionStoreService.getExamQuestionReferredExams(questionId);
        return R.successWithData(examQuestionReferredPaper);
    }

}
