package com.company.web.controller.exam;

import com.company.common.annotation.Log;
import com.company.common.annotation.RepeatSubmit;
import com.company.common.core.controller.BaseController;
import com.company.common.core.domain.R;
import com.company.common.enums.BusinessType;
import com.company.exam.form.EduUserExamineQueryForm;
import com.company.exam.form.SubmitExamineForm;
import com.company.exam.service.IEduExamRecordCollectService;
import com.company.exam.vo.UserExamineRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 考核报名记录Controller
 */
@RestController
@RequestMapping("edu/exam/record")
public class EduExamRecordCollectController extends BaseController {

    @Autowired
    private IEduExamRecordCollectService examRecordCollectService;

    /**
     * 查询【我的】考核列表
     */
    @GetMapping("list")
    public R selectExamRecordCollectList(EduUserExamineQueryForm queryForm) {
        List<UserExamineRecordVO> list = examRecordCollectService.selectExamRecordCollectList(queryForm);
        return R.successWithData(list);
    }

    /**
     * 考核报名
     */
    @PostMapping("enroll/{examId}")
    public R examQuestionList(@PathVariable Long examId) {
        examRecordCollectService.enrollExam(examId, Boolean.TRUE);
        return R.success("报名成功");
    }

    /**
     * 考试前检查一下  是否允许考试
     */
    @Log(title = "检查考试", businessType = BusinessType.UPDATE)
    @PostMapping("check/{recordCollectId}")
    public R checkBeforeExamine(@PathVariable Long recordCollectId) {
        examRecordCollectService.checkBeforeExamine(recordCollectId);
        return R.success();
    }

    /**
     * 撤销考核报名
     */
    @Log(title = "考核报名", businessType = BusinessType.UPDATE)
    @PostMapping("revokeExam/{recordCollectId}")
    public R revokeExam(@PathVariable Long recordCollectId) {
        examRecordCollectService.revokeExam(recordCollectId);
        return R.success("撤销成功");
    }


    /**
     * 提交/自动保存试卷
     */
    @RepeatSubmit
    @PostMapping("submitExamine")
    public R submitExamine(@RequestBody @Validated SubmitExamineForm form) {
        examRecordCollectService.submitExamine(form);
        return R.success("提交成功");
    }


}
