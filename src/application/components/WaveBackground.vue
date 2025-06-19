<style scoped lang="scss">
@use "sass:math"; // 添加这行导入

.wave-container {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
  overflow: hidden;
}

.waves {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  min-height: 300px;
  transform-origin: bottom;
  animation: waveScale 8s ease-in-out infinite alternate;

  path {
    mix-blend-mode: screen;
    stroke-width: 0.5;
    stroke: rgba(255, 255, 255, 0.3);
  }
}

.bubbles {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 100%;
}

.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 50%;
  filter: blur(2px);
  animation: float linear infinite;

  @for $i from 1 through 15 {
    &:nth-child(#{$i}) {
      $size: math.random(15) + 5px; // 改为 math.random()
      width: $size;
      height: $size;
      left: math.random(100) * 1%; // 改为 math.random()
      bottom: -$size;
      animation-duration: math.random(20) + 10s; // 改为 math.random()
      animation-delay: math.random(10) * -1s; // 改为 math.random()
      opacity: math.random(30) * 0.01 + 0.2; // 改为 math.random()
    }
  }
}

@keyframes float {
  to {
    transform: translateY(-100vh);
  }
}

@keyframes waveScale {
  0%, 100% {
    transform: scaleY(1) scaleX(1.02);
  }
  50% {
    transform: scaleY(1.05) scaleX(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .waves {
    min-height: 200px;
    animation: waveScaleMobile 8s ease-in-out infinite alternate;
  }

  @keyframes waveScaleMobile {
    0%, 100% {
      transform: scaleY(1) scaleX(1.01);
    }
    50% {
      transform: scaleY(1.03) scaleX(1);
    }
  }

  .bubble {
    @for $i from 1 through 10 {
      &:nth-child(#{$i}) {
        $size: math.random(10) + 3px; // 改为 math.random()
        width: $size;
        height: $size;
      }
    }
  }
}
</style>
