<template>
  <div class="app-container">

    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="考核标题" prop="examTitle">
        <el-input v-model="queryParams.examTitle" placeholder="请输入考核标题" clearable size="small" @keyup.enter.native="handleQuery"/>
      </el-form-item>
      <el-form-item>
        <el-button type="cyan" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['exam:exam:add']">新增</el-button>
      </el-col>
	    <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table id="examTable" v-loading="loading" :data="examList" border>
      <el-table-column label="考核标题" align="center" prop="title" />
      <el-table-column label="所属课程编号" align="center" prop="courseId" />
      <el-table-column label="满分" width="80" align="center" prop="totalScore"/>
      <el-table-column label="困难等级" width="150" align="center" prop="difficultyLevel">
        <template slot-scope="scope">
          <el-rate :value="scope.row.difficultyLevel" void-icon-class="icon-rate-face-off" disabled :colors="['#99A9BF', '#F7BA2A', '#FF9900']"/>
        </template>
      </el-table-column>
      <el-table-column label="封面" width="100" align="center" prop="coverImg">
        <template slot-scope="scope">
          <el-popover placement="right-end" trigger="hover">
            <img :src="scope.row.coverImg">
            <img slot="reference" :src="scope.row.coverImg" style="width: 90%;">
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="备注说明" show-overflow-tooltip align="center" prop="remark" />
      <el-table-column label="状态" width="100" align="center" prop="state">
        <template slot-scope="scope">
          <el-tag :type="scope.row.state.code == 'NORMAL' ? 'success' : 'warning'" effect="dark" v-text="scope.row.state.desc"></el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button v-show="scope.row.courseId ? true : false" size="mini" type="text" icon="el-icon-link" @click="enrollExamOpen(scope.row)">报名</el-button>
          <el-button size="mini" type="text" icon="el-icon-view" @click="toExamQuestionPage(scope.row)">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['exam:exam:edit']">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改考核对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="选择课程" prop="courseId" v-if="!form.id">
          <el-select v-model="form.courseId" placeholder="请选择课程" clearable size="small" style="width: 240px">
            <el-option v-for="course in courseList" :key="course.id" :label="course.name" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="考核标题" prop="title" required error="请输入考核标题">
          <el-input v-model="form.title" placeholder="请输入考核标题"/>
        </el-form-item>
        <el-form-item label="封面" prop="coverImg">
          <el-image v-bind:src="form.coverImg" title="点击上传头像"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="上传考核封面图片" :visible.sync="imgCropperOpen" width="800px" append-to-body>
      <ImgCropper :img="form.coverImg" :uploadDone="(url) => form.coverImg = url"></ImgCropper>
    </el-dialog>

    <el-dialog title="考核报名" :visible.sync="enrollExamDialogOpen" width="1200px" append-to-body>
      <div style="margin: 20px;">
        <div class="profile-card js-profile-card">
          <div class="profile-card__img">
            <img :src="selectedExam.coverImg" alt="profile card">
          </div>

          <div class="profile-card__cnt js-profile-cnt">
            <div class="profile-card__name" v-text="selectedExam.title"></div>
            <div class="profile-card__txt" v-text="selectedExam.remark"></div>
            <div class="profile-card-inf">
              <div class="profile-card-inf__item">
                <div class="profile-card-inf__title" v-text="selectedExam.totalScore"></div>
                <div class="profile-card-inf__txt">总分</div>
              </div>
              <div class="profile-card-inf__item">
                <div class="profile-card-inf__title" v-text="selectedExam.difficultyLevel"></div>
                <div class="profile-card-inf__txt">难度</div>
              </div>
            </div>
            <div class="profile-card-ctr">
              <button @click="confirmEnrollExam" class="profile-card__button button--blue js-message-btn">报名</button>
              <button @click="enrollExamClose" class="profile-card__button button--orange">取消</button>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import {listExam, getExam, delExam, addExam, updateExam, updateState} from "@/api/edu/exam";
import {getAll} from "@/api/system/course";
import {enrollExam} from "@/api/edu/examRecord";
import ImgCropper from '@/components/VueCropper'
import { getToken } from '@/utils/auth'

export default {
  name: "Exam",
  components: { ImgCropper },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 考核表格数据
      examList: [],
      courseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      selectedExam: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        examTitle: null,
        state: null,
        examTimeBegin: null,
        examTimeEnd: null,
      },
      // 表单参数
      form: {
        coverImg: 'https://qn.ee1.net.cn/exam-cover-default.png'
      },
      // 表单校验
      rules: {
        title: [
          { required: true, message: "考核标题不能为空", trigger: "blur" }
        ],
        courseId: [
          { required: true, message: "课程不能为空", trigger: "blur" }
        ],
      },
      timeRotateIntervalArray: [],
      timeRotating: true,
      imgCropperOpen: false,
      enrollExamDialogOpen: false,
      uploadUrl: process.env.VUE_APP_BASE_API + "/system/uploadFile",
      upload: {
        url: process.env.VUE_APP_BASE_API + "/system/uploadFile",
        headers: {
          Authorization: "Bearer " + getToken()
        }
      }
    };
  },
  created() {
    this.getList();
    this.getAllCourse();
  },
  filters: {
    totalScoreFormat(score){
      if (score > 80){
        return "#67c23a";
      } else if (score > 60){
        return "#3c7ce6";
      } else if (score > 30){
        return "#e6a23c";
      } else {
        return "#98aabb";
      }
    },
  },
  methods: {
    /** 查询考核列表 */
    getList() {
      this.loading = true;
      this.examList = [];
      this.total = 0;
      listExam(this.queryParams).then(response => {
        this.examList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    getAllCourse(){
      getAll().then(res => {
        this.courseList = res.data || []
      })
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        title: null,
        courseId: null,
        coverImg: 'https://qn.ee1.net.cn/exam-cover-default.png',
        remark: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        delFlag: null
      };
      this.resetForm("form");
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },

    toExamQuestionPage(exam){
      this.$router.push(`/exam/question/${exam.id}/${exam.title}`)
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加考核";
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getExam(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改考核";
      });
    },

    updateExamState(exam, newState, desc){
      this.$confirm(`确定${desc}考核[${exam.title}]吗？`, "警告", {
        confirmButtonText: desc,
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return updateState(exam.id, newState)
      }).then(() => {
        this.getList();
        this.msgSuccess("操作成功");
      })
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateExam(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              }
            });
          } else {
            addExam(this.form).then(response => {
              if (response.code === 200) {
                this.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              }
            });
          }
        }
      });
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除考核编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delExam(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        }).catch(function() {});
    },

    enrollExamOpen(exam){
      this.selectedExam = exam
      this.enrollExamDialogOpen = true
    },

    enrollExamClose(){
      this.selectedExam = {}
      this.enrollExamDialogOpen = false
    },

    confirmEnrollExam(){
      if (!this.selectedExam.id){
        return this.msgWarning("请选择需要报名的考核");
      }
      const loading = this.$loading({
        lock: true,
        text: '报名请求处理中！请耐心等待',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      enrollExam(this.selectedExam.id, null).then((res) => {
        this.msgSuccess("报名成功");
        this.enrollExamClose()
      }).finally(() => {
        loading.close()
      })
    },


  }
};
</script>

<style>

  .profile-card {
    width: 100%;
    min-height: 460px;
    margin: auto;
    box-shadow: 0px 8px 60px -10px rgba(13, 28, 39, 0.6);
    background: #fff;
    border-radius: 12px;
    max-width: 700px;
    position: relative;
  }
  .profile-card.active .profile-card__cnt {
    filter: blur(6px);
  }
  .profile-card__img {
    width: 150px;
    height: 150px;
    margin-left: auto;
    margin-right: auto;
    transform: translateY(-50%);
    border-radius: 50%;
    overflow: hidden;
    position: relative;
    /*z-index: 4;*/
    box-shadow: 0px 5px 50px 0px #6c44fc, 0px 0px 0px 7px rgba(107, 74, 255, 0.5);
  }
  @media screen and (max-width: 576px) {
    .profile-card__img {
      width: 120px;
      height: 120px;
    }
  }
  .profile-card__img img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
  }
  .profile-card__cnt {
    margin-top: -35px;
    text-align: center;
    padding: 0 20px;
    padding-bottom: 40px;
    transition: all .3s;
  }
  .profile-card__name {
    font-weight: 700;
    font-size: 24px;
    color: #6944ff;
    margin-bottom: 15px;
  }
  .profile-card__txt {
    font-size: 18px;
    font-weight: 500;
    color: #324e63;
    margin-bottom: 15px;
  }
  .profile-card__txt strong {
    font-weight: 700;
  }
  .profile-card-inf {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    align-items: flex-start;
    margin-top: 35px;
  }
  .profile-card-inf__item {
    padding: 10px 15px;
    min-width: 150px;
  }
  @media screen and (max-width: 768px) {
    .profile-card-inf__item {
      padding: 10px 10px;
      min-width: 120px;
    }
  }
  .profile-card-inf__title {
    font-family: fantasy;
    font-weight: 700;
    font-size: 20px;
    color: #324e63;
  }
  .profile-card-inf__txt {
    font-weight: 500;
    margin-top: 10px;
  }
  .profile-card-ctr {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 40px;
  }
  @media screen and (max-width: 576px) {
    .profile-card-ctr {
      flex-wrap: wrap;
    }
  }
  .profile-card__button {
    background: none;
    border: none;
    font-family: 'Quicksand', sans-serif;
    font-weight: 700;
    font-size: 19px;
    margin: 15px 35px;
    padding: 15px 40px;
    min-width: 201px;
    border-radius: 50px;
    min-height: 55px;
    color: #fff;
    cursor: pointer;
    backface-visibility: hidden;
    transition: all .3s;
  }
  @media screen and (max-width: 768px) {
    .profile-card__button {
      min-width: 170px;
      margin: 15px 25px;
    }
  }
  @media screen and (max-width: 576px) {
    .profile-card__button {
      min-width: inherit;
      margin: 0;
      margin-bottom: 16px;
      width: 100%;
      max-width: 300px;
    }
    .profile-card__button:last-child {
      margin-bottom: 0;
    }
  }
  .profile-card__button:focus {
    outline: none !important;
  }
  @media screen and (min-width: 768px) {
    .profile-card__button:hover {
      transform: translateY(-5px);
    }
  }
  .profile-card__button:first-child {
    margin-left: 0;
  }
  .profile-card__button:last-child {
    margin-right: 0;
  }
  .profile-card__button.button--blue {
    background: linear-gradient(45deg, #1da1f2, #0e71c8);
    box-shadow: 0px 4px 30px rgba(19, 127, 212, 0.4);
  }
  .profile-card__button.button--blue:hover {
    box-shadow: 0px 7px 30px rgba(19, 127, 212, 0.75);
  }
  .profile-card__button.button--orange {
    background: linear-gradient(45deg, #d5135a, #f05924);
    box-shadow: 0px 4px 30px rgba(223, 45, 70, 0.35);
  }
  .profile-card__button.button--orange:hover {
    box-shadow: 0px 7px 30px rgba(223, 45, 70, 0.75);
  }
  .profile-card textarea {
    width: 100%;
    resize: none;
    height: 210px;
    margin-bottom: 20px;
    border: 2px solid #dcdcdc;
    border-radius: 10px;
    padding: 15px 20px;
    color: #324e63;
    font-weight: 500;
    font-family: 'Quicksand', sans-serif;
    outline: none;
    transition: all .3s;
  }
  .profile-card textarea:focus {
    outline: none;
    border-color: #8a979e;
  }


</style>
