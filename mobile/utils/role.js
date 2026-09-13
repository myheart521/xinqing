export function getRoleName(roleId) {
    const roleMap = {
        1: '管理员',
        2: '教师',
        3: '学生',
        4: '游客'
    }
    return roleMap[roleId] || '游客'
}