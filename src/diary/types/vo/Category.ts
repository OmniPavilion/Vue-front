export enum Category {
    STUDY = "Study",
    WORK = "Work",
    LIFE = "Life",
    EXERCISE = "Exercise",
    ENTERTAINMENT = "Entertainment",
    SOCIAL = "Social",
}

// 分类对应的中文名称和颜色
export const CategoryInfo = {
    [Category.STUDY]: { name: '学习', color: '#FF9AA2' },
    [Category.WORK]: { name: '工作', color: '#FFB7B2' },
    [Category.LIFE]: { name: '生活', color: '#FFDAC1' },
    [Category.EXERCISE]: { name: '运动', color: '#E2F0CB' },
    [Category.ENTERTAINMENT]: { name: '娱乐', color: '#B5EAD7' },
    [Category.SOCIAL]: { name: '社交', color: '#C7CEEA' }
};

// 分类选项
export const CategoryOptions = (Object.keys(Category) as Array<keyof typeof Category>)
    .filter(key => isNaN(Number(key)))
    .map(key => ({
        value: key,
        label: CategoryInfo[Category[key]].name,
    }));
