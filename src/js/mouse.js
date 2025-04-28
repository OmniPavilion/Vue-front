let lastColor = null;

// 鼠标移动事件
document.addEventListener('mousemove', function (e) {
    // 创建一个拖尾元素
    const trail = document.createElement('div');
    trail.classList.add('trail');
    // 设置渐变颜色
    const color = getGradientColor();
    trail.style.backgroundColor = color;
    // 设置初始大小
    trail.style.width = '20px';
    trail.style.height = '20px';
    // 设置位置
    trail.style.left = e.clientX - 10 + 'px';
    trail.style.top = e.clientY - 10 + 'px';
    // 设置 z-index 确保在顶部
    trail.style.zIndex = 99999; 
    // 添加到文档中
    document.body.appendChild(trail);

    // 一段时间后移除元素
    setTimeout(() => {
        trail.remove();
    }, 1000);
});

// 鼠标点击事件
document.addEventListener('click', function (e) {
    const numParticles = 20;
    for (let i = 0; i < numParticles; i++) {
        // 创建一个点击特效元素
        const particle = document.createElement('div');
        particle.classList.add('click-effect');
        // 设置随机颜色
        particle.style.backgroundColor = getRandomColor();
        // 设置初始大小
        particle.style.width = '10px';
        particle.style.height = '10px';
        // 设置位置
        particle.style.left = e.clientX - 5 + 'px';
        particle.style.top = e.clientY - 5 + 'px';
        // 设置 z-index 确保在顶部
        particle.style.zIndex = 99999; 
        // 添加到文档中
        document.body.appendChild(particle);

        // 计算抛物线轨迹
        const angle = Math.random() * 2 * Math.PI;
        const distance = Math.random() * 200;
        const x = e.clientX + distance * Math.cos(angle);
        const y = e.clientY + distance * Math.sin(angle) - 0.5 * (distance / 20) ** 2;

        // 动画移动
        const animation = particle.animate([
            { transform: `translate(0, 0)`, opacity: 1 },
            { transform: `translate(${x - e.clientX}px, ${y - e.clientY}px)`, opacity: 0 }
        ], {
            duration: 1000,
            easing: 'ease-out',
            fill: 'forwards'
        });

        // 动画结束后移除元素
        animation.onfinish = () => {
            particle.remove();
        };
    }
});

// 生成随机颜色
function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

// 生成渐变颜色
function getGradientColor() {
    if (!lastColor) {
        lastColor = getRandomColor();
    }
    const rgb = hexToRgb(lastColor);
    const maxDelta = 20;
    const r = Math.min(255, Math.max(0, rgb.r + getRandomDelta(maxDelta)));
    const g = Math.min(255, Math.max(0, rgb.g + getRandomDelta(maxDelta)));
    const b = Math.min(255, Math.max(0, rgb.b + getRandomDelta(maxDelta)));
    lastColor = rgbToHex(r, g, b);
    return lastColor;
}

// 十六进制颜色转 RGB
function hexToRgb(hex) {
    const bigint = parseInt(hex.slice(1), 16);
    const r = (bigint >> 16) & 255;
    const g = (bigint >> 8) & 255;
    const b = bigint & 255;
    return { r, g, b };
}

// RGB 转十六进制颜色
function rgbToHex(r, g, b) {
    return '#' + [r, g, b].map(x => {
        const hex = x.toString(16);
        return hex.length === 1 ? '0' + hex : hex;
    }).join('');
}

// 获取随机偏移量
function getRandomDelta(maxDelta) {
    return Math.floor(Math.random() * (2 * maxDelta + 1)) - maxDelta;
}