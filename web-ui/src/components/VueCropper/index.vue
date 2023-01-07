<template>

  <div :option="options" :img="img" :uploadDone="uploadDone">
    <el-row>
      <el-col :xs="24" :md="12" :style="{height: '350px'}">
        <vue-cropper
          ref="cropper"
          :img="imgUrl"
          :info="true"
          :autoCrop="options.autoCrop"
          :autoCropWidth="options.autoCropWidth"
          :autoCropHeight="options.autoCropHeight"
          :fixedBox="options.fixedBox"
          @realTime="(data) => {this.previews = data;}"
        />
      </el-col>
      <el-col :xs="24" :md="12" :style="{height: '350px'}">
        <div class="avatar-upload-preview-original">
          <img :src="previews.url" :style="previews.img" />
        </div>
      </el-col>
    </el-row>
    <br />
    <el-row>
      <el-col :lg="2" :md="2">
        <el-upload action="#" :http-request="() => {}" :show-file-list="false" :before-upload="beforeUpload" accept=".png,.jpeg,.jpg,.png,.bmp,.gif">
          <el-button size="small">
            上传
            <i class="el-icon-upload el-icon--right"></i>
          </el-button>
        </el-upload>
      </el-col>
      <el-col :lg="{span: 1, offset: 2}" :md="2">
        <el-button icon="el-icon-plus" size="small" @click="changeScale(1)"></el-button>
      </el-col>
      <el-col :lg="{span: 1, offset: 1}" :md="2">
        <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)"></el-button>
      </el-col>
      <el-col :lg="{span: 1, offset: 1}" :md="2">
        <el-button icon="el-icon-refresh-left" size="small" @click="$refs.cropper.rotateLeft()"></el-button>
      </el-col>
      <el-col :lg="{span: 1, offset: 1}" :md="2">
        <el-button icon="el-icon-refresh-right" size="small" @click="$refs.cropper.rotateRight()"></el-button>
      </el-col>
      <el-col :lg="{span: 2, offset: 6}" :md="2">
        <el-button type="primary" size="small" @click="uploadImg()">提 交</el-button>
      </el-col>
    </el-row>
  </div>

</template>

<script>


import {uploadFile} from "@/api/system/common";
import { VueCropper } from "vue-cropper";

export default {
  name: 'ImgCropper',
  components: {
    VueCropper
  },
  props: {
    img: {
      type: String,
      default: null
    },
    uploadDone: {
      type: Function
    },
    options: {
      type: Object,
      default: () => {
        return {
            autoCrop: true, // 是否默认生成截图框
            autoCropWidth: 200, // 默认生成截图框宽度
            autoCropHeight: 200, // 默认生成截图框高度
            fixedBox: true // 固定截图框大小 不允许改变
        }
      }
    }
  },
  data() {
    return {
      imgUrl: this.img,
      previews: {},
    }
  },
  methods: {
    // 上传预处理
    beforeUpload(file) {
      if (file.type.indexOf("image/") == -1) {
        this.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
      } else {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
          this.imgUrl = reader.result;
        };
      }
    },

    // 图片缩放
    changeScale(num) {
      num = num || 1;
      this.$refs.cropper.changeScale(num);
    },

    // 上传图片
    uploadImg() {
      this.$refs.cropper.getCropBlob((data) => {
        let formData = new FormData()
        formData.append("file", data)
        uploadFile(formData).then(response => {
          if (response.code === 200) {
            this.imgUrl = response.data;
            this.uploadDone(this.imgUrl)
            this.msgSuccess("上传成功");
          }
        });
      });
    },

  }
}
</script>


<style lang="scss" scoped>


</style>
