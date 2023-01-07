package com.company.web.controller.exam;

import java.util.List;

import com.company.common.annotation.RepeatSubmit;
import com.company.exam.form.EduExamQuestionQueryForm;
import com.company.exam.vo.ExamQuestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.company.common.annotation.Log;
import com.company.common.core.controller.BaseController;
import com.company.common.core.domain.R;
import com.company.common.enums.BusinessType;
import com.company.exam.service.IEduExamQuestionService;
import com.company.common.utils.poi.ExcelUtil;

/**
 * 考试试卷题目Controller
 */
@RestController
@RequestMapping("/edu/exam/question")
public class EduExamQuestionController extends BaseController {

    @Autowired
    private IEduExamQuestionService eduExamQuestionService;

    /**
     * 查询考试试卷题目列表
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:list')")
//    @GetMapping("list")
//        public TableDataInfo list(EduExamQuestionQueryForm queryForm) {
//        startPage();
//        List<EduExamQuestion> list = eduExamQuestionService.selectEduExamQuestionList(queryForm);
//        return super.getDataTable(list);
//    }

    /**
     * 导出考试试卷题目列表
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:export')")
    @Log(title = "考试试卷题目", businessType = BusinessType.EXPORT)
    @GetMapping("export")
    public R export(EduExamQuestionQueryForm queryForm) {
        List<ExamQuestionVO> list = eduExamQuestionService.selectEduExamQuestionList(queryForm);
        ExcelUtil<ExamQuestionVO> util = new ExcelUtil<>(ExamQuestionVO. class);
        return R.successWithData(util.exportExcel(list, "question"));
    }

    /**
     * 获取考试试卷题目详细信息
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:query')")
    @GetMapping(value = "{id}")
    public R getInfo(@PathVariable("id") Long id) {
        return R.successWithData(eduExamQuestionService.selectEduExamQuestionById(id));
    }

    /**
     * 删除考试试卷题目
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:remove')")
//    @Log(title = "考试试卷题目", businessType = BusinessType.DELETE)
//    @DeleteMapping("{ids}")
//    public R remove(@PathVariable Long[] ids) {
//        eduExamQuestionService.deleteEduExamQuestionByIds(ids);
//        return R.success("删除成功");
//    }

    /**
     * 删除考试试卷题目
     */
//    @PreAuthorize("@ss.hasPermi('exam:question:remove')")
    @Log(title = "考试试卷题目", businessType = BusinessType.DELETE)
    @DeleteMapping("{id}")
    public R remove(@PathVariable Long id) {
        eduExamQuestionService.deleteEduExamQuestionById(id);
        return R.success("删除成功");
    }

    /**
     * 从题库导入试题到考核试卷[单个、批量]
     */
    @Log(title = "批量从题库导入试题到考核试卷")
    @RepeatSubmit
    @PostMapping("importByQuestionIds")
    public R importByQuestionIds(@RequestParam Long examId, @RequestParam Long[] storeQuestionIds) {
        eduExamQuestionService.importByQuestionIds(examId, storeQuestionIds);
        return R.success("导入成功");
    }

    /**
     * 直接拷贝某个试卷的所有试题到考核
     */
    @Log(title = "拷贝某个试卷的所有试题到考核")
    @RepeatSubmit
    @PostMapping("importByPaperQuestion/{examId}/{paperId}")
    public R importByPaperQuestion(@PathVariable Long examId, @PathVariable Long paperId) {
        eduExamQuestionService.importByPaperQuestion(examId, paperId);
        return R.success("导入成功");
    }

    /**
     * 调换考核试卷试题的顺序
     */
    @RepeatSubmit(time = 3000)
    @PostMapping("changeExamQuestionSort")
    public R changeExamQuestionSort(@RequestParam Long fromId, @RequestParam Long toId, @RequestParam String opt) {
        eduExamQuestionService.changeExamQuestionSort(opt, fromId, toId);
        return R.success("操作成功");
    }

}
