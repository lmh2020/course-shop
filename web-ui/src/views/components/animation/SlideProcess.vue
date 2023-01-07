<template>
  <main :process="process" :status="status" class="perspective" id="slideProcess">
    <section class="bar">
      <div class="bar-face front percentage"></div>
      <div class="bar-face back percentage"></div>
      <div class="bar-face floor percentage"></div>
      <div class="bar-face roof percentage"></div>
      <div class="indicator"></div>
    </section>
    <input id="barInput" class="bar-input" type="range" min="0" max="100" :value="process"/>
  </main>

</template>

<script>

export default {
  components: {},
  props: {
    process: {
      type: Number,
    },
    status: {
      type: String,
      default: null,
      required: false
    },
  },
  data() {
    return {


    }
  },
  watch: {
    process(newVal, oldVal) {
      if (newVal >=0 && newVal <= 100 && newVal > oldVal){
        this.refreshProcess(newVal)
      }
    }
  },
  mounted() {
    this.refreshProcess(0)
  },

  methods: {

    /* 刷新进度 */
    refreshProcess(newVal) {
      const indicator = document.getElementById("barInput").parentNode.querySelector('.bar .indicator');
      /* TODO 旧的style元素不会清理 导致生成的style过多影响页面效率 */
      document.head.appendChild(document.createElement("style")).innerHTML = "#slideProcess .bar-face.percentage:before {width:" + newVal + "%;transition:width 0.5s;}";
      indicator.style.marginLeft = (newVal - 3) + '%';
      indicator.style.transition = 'margin 0.5s';
      indicator.textContent = this.status == 'ERROR' ? '失败' : (newVal + '%');
    },

  }
}
</script>


<style lang="scss" scoped>

.perspective {
  -webkit-perspective: 70vw;
  perspective: 70vw;
  text-align: center;
  -webkit-perspective-origin: 50% 50%;
  perspective-origin: 50% 50%;
  position: relative;
  transition: -webkit-transform 0.3s ease;
  transition: transform 0.3s ease;
  transition: transform 0.3s ease, -webkit-transform 0.3s ease;
}

.perspective:hover {
  -webkit-transform: scale(1.04);
  transform: scale(1.04);
}

.bar-input {
  position: absolute;
  height: 100%;
  left: 0;
  right: 0;
  margin: auto;
  opacity: 0;
}

#slideProcess .bar-input {
  width: 42vw;
}

.bar {
  display: inline-block;
  position: relative;
  -webkit-transform: rotateX(55deg);
  transform: rotateX(55deg);
  -webkit-transform-style: preserve-3d;
  transform-style: preserve-3d;
}

.bar .bar-face {
  display: inline-block;
  width: 100%;
  height: 100%;
  position: absolute;
  left: 0;
  -webkit-transform-origin: 50% 100%;
  transform-origin: 50% 100%;
}

.bar .bar-face.front {
  -webkit-transform: rotateX(-90deg);
  transform: rotateX(-90deg);
}

.bar .bar-face.percentage:before {
  height: 100%;
  content: "";
  display: block;
  position: absolute;
  bottom: 0;
  margin: 0;
}

#slideProcess .bar {
  width: 42vw;
  height: 1.5vw;
}

#slideProcess .bar .bar-face {
  background: rgba(162, 162, 162, 0.72);
}

#slideProcess .bar .bar-face.floor {
  box-shadow: 0 1.3em 1.2em -0.4em rgba(0, 0, 70, 0.25), 0 -2em 15em 0.5em #4d5075, 0 -0.75em 25em 10em rgba(255, 255, 255, 0.4);
}

#slideProcess .bar .bar-face.percentage:before {
  //box-shadow: 0 1.6em 7em -0.3em rgba(236, 0, 113, 0.5);
  box-shadow: 0 1.5em 2.5em -0.8em rgb(57, 134, 45);
  background: linear-gradient(90deg, rgba(255, 0, 38, 0.41), #35d63b);
}

#slideProcess .bar .bar-face.roof {
  -webkit-transform: translateZ(1.5vw);
  transform: translateZ(1.5vw);
}

#slideProcess .bar .bar-face.back {
  -webkit-transform: rotateX(-90deg) translateZ(-1.5vw);
  transform: rotateX(-90deg) translateZ(-1.5vw);
}

#slideProcess .bar .indicator {
  box-shadow: 0px 15px 35px rgba(236, 0, 113, 0.3);
  background: #ec0071;
  width: 4vw;
  height: 3.5vw;
  color: white;
  -webkit-transform: translateY(-7vw);
  transform: translateY(-7vw);
  text-align: center;
  font-size: 1vw;
  font-weight: 900;
  line-height: 3vw;
}

#slideProcess .bar .indicator:before {
  content: "";
  position: absolute;
  background: #ec0071;
  left: 0;
  right: 0;
  margin: auto;
  top: 65px;
  width: 1vw;
  height: 1vw;
  z-index: -1;
  -webkit-transform: rotate(45deg);
  transform: rotate(45deg);
}

</style>
