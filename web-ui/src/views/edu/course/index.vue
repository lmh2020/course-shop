<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程名" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入课程名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:course:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <el-table v-loading="loading" :data="courseList" border>
      <el-table-column label="标题" align="center" prop="id" />
      <el-table-column label="封面" align="center" prop="img">
        <template slot-scope="scope">
          <el-popover placement="right-end" trigger="hover">
            <img :src="scope.row.img">
            <img slot="reference" :src="scope.row.img" style="width: 100px;">
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="课程名" align="center" prop="name">
        <template slot-scope="scope">
          <el-link @click="videoUrl=scope.row.linkUrl;videoOpen=true" style="font-weight: 800;font-size: 15px;color: green;">{{scope.row.name}}<i class="el-icon-view el-icon--right"></i> </el-link>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" />
      <el-table-column label="礼品" align="center" prop="gift" />
      <el-table-column label="价格" align="center" prop="price" />
      <el-table-column label="销量" align="center" prop="sales" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:course:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:course:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改课程对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名" prop="name">
          <el-input v-model="form.name" placeholder="请输入课程名" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="封面图" prop="img">
          <el-upload
            class="avatar-uploader"
            :headers="upload.headers"
            :action="upload.url"
            accept="image/*"
            :show-file-list="false"
            :on-success="(res, file, fileList) => this.form.img = res.data.url"
            :before-upload="beforeUploadImg">
            <img v-if="form.img" :src="form.img" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"/>
          </el-upload>
        </el-form-item>
        <el-form-item label="访问链接" prop="linkUrl">
          <el-upload
            class="avatar-uploader video"
            :headers="upload.headers"
            :action="upload.url2"
            accept="video/*"
            :show-file-list="true"
            :limit="1"
            :on-remove="() => form.linkUrl=null"
            :on-exceed="() => $message.warning('请先移除旧文件')"
            :file-list="fileList"
            :on-success="(res, file, fileList) => {uploadLoading.close();$message.success('上传成功！');this.form.linkUrl = res.data}"
            :before-upload="beforeUpload">
            <i class="el-icon-upload"></i>
            <div class="el-upload__text"><em>点击上传</em></div>
          </el-upload>
        </el-form-item>
        <el-form-item label="说明" prop="detail">
          <el-input v-model="form.detail" type="textarea" maxlength="500" show-word-limit placeholder="请输入说明"/>
        </el-form-item>
        <el-form-item label="礼品" prop="gift">
          <el-tag :key="tag" v-for="tag in dynamicTags" closable :disable-transitions="false" @close="handleClose(tag)">{{tag}}</el-tag>
          <el-input class="input-new-tag" v-if="inputVisible" v-model="inputValue" style="width: 80px;"
                    ref="saveTagInput" size="small" @keyup.enter.native="handleInputConfirm" @blur="handleInputConfirm"/>
          <el-button v-else class="button-new-tag" size="small" @click="showInput">+ 添 加</el-button>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :precision="0" :step="1" :min="1" :max="999" placeholder="请输入价格"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="课程视频" :visible.sync="videoOpen" width="800px" append-to-body>
      <video controls :src="videoUrl" style="width: 100%;"/>
    </el-dialog>

  </div>
</template>

<script>
import { listCourse, getCourse, delCourse, addCourse, updateCourse, exportCourse } from "@/api/system/course";
import { getToken } from '@/utils/auth'
import VideoPlayer from 'vue-video-player'
require('video.js/dist/video-js.css')
require('vue-video-player/src/custom-theme.css')

export default {
  name: "Course",
  components: {
    VideoPlayer
  },
  data() {
    return {
      dynamicTags: [],
      inputVisible: false,
      inputValue: null,
      fileList: [],
      // 遮罩层
      loading: true,
      uploadLoading: null,
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
      // 课程表格数据
      courseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      videoOpen: false,
      videoUrl: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        title: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "课程名不能为空", trigger: "blur" }
        ],
        img: [
          { required: true, message: "封面图不能为空", trigger: "change" }
        ],
        title: [
          { required: true, message: "课程名不能为空", trigger: "blur" }
        ],
        price: [
          { required: true, message: "价格不能为空", trigger: "blur" }
        ]
      },
      upload: {
        // 设置上传的请求头部
        headers: { Authorization: "Bearer " + getToken() },
        // 上传的地址
        url: process.env.VUE_APP_BASE_API + "/common/upload",
        url2: process.env.VUE_APP_BASE_API + "/common/oss/upload",
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询课程列表 */
    getList() {
      this.loading = true;
      listCourse(this.queryParams).then(response => {
        this.courseList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        userId: null,
        examId: null,
        linkUrl: null,
        name: null,
        title: null,
        img: null,
        detail: null,
        createTime: null,
        price: null,
        gift: null,
      };
      this.dynamicTags = [],
      this.inputVisible = false,
      this.inputValue = null,
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加课程";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getCourse(id).then(response => {
        this.form = response.data;
        if (response.data.gift){
          this.dynamicTags = response.data.gift.split(",");
        }
        this.fileList = [{name: response.data.name, url: response.data.linkUrl}]
        this.open = true;
        this.title = "修改课程";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (!this.form.linkUrl){
            return this.msgError("请上传视频");
          }
          this.form.gift = this.dynamicTags.join(",")
          if (this.form.id != null) {
            updateCourse(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCourse(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除课程编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delCourse(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有课程数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportCourse(queryParams);
        }).then(response => {
          this.download(response.msg);
        })
    },

    beforeUpload(file){
      if (file.type.indexOf("video") < 0) {
        this.$message.error('上传图片只能是视频格式!'); return false;
      }else if (file.size / 1024 / 1024 > 2048) {
        this.$message.error('上传视频大小不能超过 2048MB!'); return false;
      } else if (file.name.length > 60){
        this.$message.error('文件名字符长度过长！'); return false;
      }
      this.uploadLoading = this.$loading({lock: true,text: '上传中...', spinner: 'el-icon-loading',background: 'rgba(0, 0, 0, 0.7)'});
      setTimeout(() => this.uploadLoading.close(), 30000);
      return true;
    },

    beforeUploadImg(file){
      if (file.type.indexOf("image/") == -1) {
        this.$message.error('上传图片只能是图片格式!'); return false;
      }else if (file.size / 1024 / 1024 > 5) {
        this.$message.error('上传图片大小不能超过 5MB!'); return false;
      } else if (file.name.length > 60){
        this.$message.error('文件名字符长度过长！'); return false;
      }
      return true;
    },

    handleClose(tag) {
      this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue) {
        this.dynamicTags.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = '';
    }

  }
};
</script>

<style>
.rotating-text {
  font-weight: 600;
  text-align: center;
  width: 80%;
  background-color: #ecf5ff;
  display: inline-block;
  height: 32px;
  line-height: 35px;
  color: #409eff;
  border: 1px solid #d9ecff;
  border-radius: 4px;
  white-space: nowrap;
  overflow: hidden;
}
.rotating-text p {
  display: inline-flex;
  margin: 0;
  float: left;
  vertical-align: top;
}
.rotating-text p .word {
  overflow: hidden;
  position: absolute;
  display: flex;
  opacity: 0;
}
.rotating-text p .word .letter {
  transform-origin: center center 25px;
}
.rotating-text p .word .letter.out {
  transform: rotateX(90deg);
  transition: 0.32s cubic-bezier(0.6, 0, 0.7, 0.2);
}
.rotating-text p .word .letter.in {
  transition: 0.38s ease;
}
.rotating-text p .word .letter.behind {
  transform: rotateX(-90deg);
}

.alizarin {
  margin-left: 10px;
  color: #2a70d8;
}

.wisteria {
  margin-left: 40px;
  color: #8e44ad;
}

.heart {
  background-color: #ffa5ae;
  animation: heartbeat 1.25s infinite;
}

@keyframes heartbeat {
  0% {
    transform: scale(1);
  }
  10% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.avatar-uploader.video .el-upload {
  width: 300px;
  height: 100px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.el-tag + .el-tag {
  margin-left: 10px;
}
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}
.input-new-tag {
  width: 90px;
  margin-left: 10px;
  vertical-align: bottom;
}

</style>
