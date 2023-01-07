package com.company.web.controller.exam;

import java.util.List;
import java.util.Map;

import com.company.common.enums.business.QuestionTypeEnum;
import com.company.common.validator.group.ValidGroup;
import com.company.exam.form.EduExamQuestionForExamQueryForm;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.service.IEduPaperStoreService;
import com.company.exam.vo.ExamQuestionVO;
import com.company.exam.vo.ExamineVO;
import com.company.exam.vo.StoreQuestionVO;
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
import com.company.exam.form.EduExamMergeForm;
import com.company.exam.form.EduExamQueryForm;
import com.company.exam.domain.EduExam;
import com.company.exam.service.IEduExamService;
import com.company.common.utils.poi.ExcelUtil;
import com.company.common.core.page.TableDataInfo;

import javax.validation.Valid;

/**
 * 考核Controller
 */
@RestController
@RequestMapping("edu/exam")
public class EduExamController extends BaseController {

    @Autowired
    private IEduExamService eduExamService;

    @Autowired
    private IEduPaperStoreService eduPaperStoreService;

    /**
     * 查询考核列表
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:list')")
    @GetMapping("list")
    public TableDataInfo list(EduExamQueryForm queryForm) {
        startPage();
        List<EduExam> list = eduExamService.selectEduExamList(queryForm);
        return super.getDataTable(list);
    }

    /**
     * 分页从题库里查询【可导入某#考核#】的所有试题（对题库结果进行了封装）
     */
    @GetMapping("storeQuestionListForExam")
    public TableDataInfo storeQuestionListForExam(@Validated EduExamQuestionForExamQueryForm queryForm) {
        startPage();
        List<StoreQuestionVO> eduQuestionStores = eduPaperStoreService.selectStoreQuestionForExam(queryForm);
        return super.getDataTable(eduQuestionStores);
    }

    /**
     * 查询考核试题列表
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:list')")//TODO
    @GetMapping("examQuestionList")
    public R examQuestionList(@Valid EduExamQuestionQueryForm queryForm) {
        Map<QuestionTypeEnum, List<ExamQuestionVO>> result = eduExamService.selectExamQuestionList(queryForm);
        return R.successWithData(result);
    }

    /**
     * 查询考试信息（用于进行考试）
     */
    @GetMapping("examineDetail/{recordCollectId}")
    public R selectExamineDetail(@PathVariable Long recordCollectId) {
        ExamineVO examineVO = eduExamService.selectExamineDetail(recordCollectId);
        return R.successWithData(examineVO);
    }

    /**
     * 查询考试信息（用于查询考试结果）
     */
    @GetMapping("examineResult/{recordCollectId}")
    public R selectExamineResult(@PathVariable Long recordCollectId) {
        ExamineVO examineVO = eduExamService.selectExamineResult(recordCollectId);
        return R.successWithData(examineVO);
    }

    /**
     * 导出考核列表
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:export')")
    @Log(title = "考核", businessType = BusinessType.EXPORT)
    @GetMapping("export")
    public R export(EduExamQueryForm queryForm) {
        List<EduExam> list = eduExamService.selectEduExamList(queryForm);
        ExcelUtil<EduExam> util = new ExcelUtil<EduExam>(EduExam. class);
        return R.successWithData(util.exportExcel(list, "exam"));
    }

    /**
     * 获取考核详细信息
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:query')")
    @GetMapping(value = "{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(eduExamService.selectEduExamById(id));
    }

    /**
     * 新增考核
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:add')")
    @Log(title = "考核", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody @Validated(ValidGroup.Insert.class) EduExamMergeForm mergeForm) {
        eduExamService.insertEduExam(mergeForm);
        return R.success("添加成功");
    }

    /**
     * 修改考核
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:edit')")
    @Log(title = "考核", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody @Validated(ValidGroup.Update.class) EduExamMergeForm mergeForm) {
        eduExamService.updateEduExam(mergeForm);
        return R.success("修改成功");
    }

    /**
     * 删除考核
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:remove')")
    @Log(title = "考核", businessType = BusinessType.DELETE)
    @DeleteMapping("{ids}")
    public R remove(@PathVariable Long[] ids) {
        eduExamService.deleteEduExamByIds(ids);
        return R.success("删除成功");
    }

    /**
     * 修改状态【考核】
     */
//    @PreAuthorize("@ss.hasPermi('edu:exam:edit')")
    @Log(title = "考核", businessType = BusinessType.UPDATE)
    @PostMapping("updateState/{examId}/{newState}")
    public R updateState(@PathVariable Long examId, @PathVariable String newState) {
        eduExamService.updateState(examId, newState);
        return R.success("操作成功");
    }


}
