declare namespace API {
  type AccountLoginDTO = {
    account?: string
    password?: string
  }

  type ActivityDTO = {
    title?: string
    content?: string
    imageList?: string[]
    startTime?: string
    endTime?: string
    label?: string
    color?: string
    address?: string
    longitude?: number
    latitude?: number
  }

  type analysisParams = {
    id: number
  }

  type AnswerVO = {
    moduleName?: string
    typeAndResult?: Record<string, any>
    description?: string
    image?: string
  }

  type aParams = {
    arg: string
  }

  type BannerDTO = {
    imageUrl?: string
    link?: string
    createId?: number
    mainNotNull?: boolean
  }

  type BasePageDTO = {
    current?: number
    pageSize?: number
  }

  type BlogDTO = {
    title?: string
    tags?: string[]
    images?: string[]
    content?: string
    circleId?: number
  }

  type Body = {
    id?: number
    height?: number
    weight?: number
    bmi?: number
    createTime?: string
    tiZhiLv?: number
    userId?: number
    equipId?: string
  }

  type BodyReportVO = {
    dateList?: string
    heightList?: string
    weightList?: string
    bmiList?: string
    tiZhiLvList?: string
  }

  type checkEmail1Params = {
    deviceId: 'Running' | 'Body' | 'smartWatch'
  }

  type collectionParams = {
    id: number
  }

  type Comments = {
    id?: number
    userId?: number
    blogId?: number
    parentId?: number
    answerId?: number
    content?: string
    liked?: number
    commentCount?: number
    status?: number
    createTime?: string
    updateTime?: string
  }

  type createEquipmentParams = {
    deviceType: 'Running' | 'Body' | 'smartWatch'
  }

  type dataReport1Params = {
    begin: string
    end: string
  }

  type dataReport2Params = {
    moduleId: number
    recent: number
  }

  type dataReport3Params = {
    begin: string
    end: string
  }

  type dataReportParams = {
    begin: string
    end: string
  }

  type delectById1Params = {
    id: number
  }

  type delectById2Params = {
    id: number
  }

  type delectByIdParams = {
    id: number
  }

  type deleteArticleParams = {
    articleId: number
  }

  type deleteBannerParams = {
    bannerId: number
  }

  type deleteCardParams = {
    cardId: number
  }

  type deleteDietParams = {
    dietId: number
  }

  type deleteUsingDELETEParams = {
    id: number
  }

  type DietDTO = {
    title?: string
    name?: string
    mainImage?: string
    content?: string
    tags?: string
    userId?: number
    mainNotNull?: boolean
  }

  type DisplayRecommendation = {
    charts?: ('RADAR_CHART' | 'WORD_CLOUD' | 'LINE_CHART' | 'BAR_CHART')[]
    reason?: string
  }

  type Doctor = {
    id?: number
    userId?: number
    background?: string
    doctorName?: string
    title?: string
    pccId?: number
    gender?: string
    rating?: number
    specializedFields?: string
    otherInformation?: string
    photo?: string
  }

  type EmailCheckDTO = {
    email?: string
  }

  type EmailLoginDTO = {
    email?: string
    password?: string
  }

  type FeedbackDTO = {
    feedBackMessage?: string
    typeId?: number
    content?: string
    userId?: number
    phone?: string
    imageList?: string[]
    startDateTime?: string
    endDateTime?: string
  }

  type FeedbackPage = {
    pageNo?: number
    pageSize?: number
    isAsc?: boolean
    sortBy?: string
    typeId?: number
    content?: string
    userId?: number
    startDateTime?: string
    endDateTime?: string
  }

  type follow1Params = {
    id: number
    isFollow: boolean
  }

  type follow2Params = {
    id: number
    isFollow: boolean
  }

  type followerListParams = {
    current?: number
  }

  type followOrNot1Params = {
    id: number
  }

  type followOrNot2Params = {
    id: number
  }

  type followOrNotParams = {
    id: number
  }

  type followParams = {
    id: number
    isFollow: boolean
  }

  type generationParams = {
    message?: string
  }

  type generationStreamParams = {
    message?: string
    id: number
  }

  type getAll1Params = {
    id: number
  }

  type getArticleParams = {
    pageDTO: BasePageDTO
    type: string
  }

  type getBanner1Params = {
    pageDTO: PageDTO
  }

  type getByIdParams = {
    id: number
  }

  type getDefaultTestParams = {
    pageDTO: PageDTO
  }

  type getDietPages1Params = {
    pageDTO: PageDTO
  }

  type getDietPages2Params = {
    id: number
  }

  type getDietPagesParams = {
    id: number
  }

  type getHistoryParams = {
    receiverId: number
    currentPage: number
    pageSize?: number
  }

  type getHistoryRecentParams = {
    pageDTO: PageDTO
  }

  type getHistoryTestDetailParams = {
    id: number
  }

  type getKnowledgeCollectionsPagesParams = {
    pageDTO: PageDTO
  }

  type getKnowledgePagesParams = {
    pageDTO: PageDTO
  }

  type getListParams = {
    current?: number
  }

  type getMusicPagesParams = {
    pageDTO: PageDTO
  }

  type getNewDateParams = {
    deviceType: 'Running' | 'Body' | 'smartWatch'
  }

  type getPCC1Params = {
    id: number
  }

  type GetPCCDatailVO = {
    phoneNumber?: string
    email?: string
    location?: string
    publicAccount?: string
    method?: string
    school?: string
    description?: string
    province?: string
    src?: string
    doctorList?: Doctor[]
    timeSlots?: TimeSlot[]
  }

  type getPCCParams = {
    introductionPCCDTO: IntroductionPCCDTO
  }

  type GetPCCVO = {
    id?: number
    phoneNumber?: string
    email?: string
    location?: string
    school?: string
    src?: string
    province?: string
  }

  type getStuInfoParams = {
    stuId: number
    params?: string[]
  }

  type getSwiperParams = {
    pageDTO: PageDTO
  }

  type HistoryTestPageVO = {
    id?: number
    title?: string
    src?: string
    createTime?: string
  }

  type HistoryTestReportVO = {
    xaxis?: string[]
    yaxis?: Record<string, any>
  }

  type HotArticleVO = {
    id?: number
    title?: string
    descriptions?: string
    coverImage?: string
    color?: string
    tags?: string
    viewCount?: number
    likeCount?: number
    collectionCount?: number
    createTime?: string
    updateTime?: string
    userName?: string
  }

  type IntroductionPCCDTO = {
    province?: string
    school?: string
  }

  type JSONObject = {
    empty?: boolean
    innerMap?: Record<string, any>
  }

  type likeBlog1Params = {
    id: number
  }

  type likeBlogParams = {
    id: number
  }

  type likeParams = {
    id: number
  }

  type list1Params = {
    nootBookDTO: NootBookDTO
  }

  type LoginDTO = {
    code?: string
  }

  type LoginVO = {
    token?: string
    user?: UserVO
  }

  type MbtiTestVO = {
    res?: string
    geteRate?: string
    getiRate?: string
    getsRate?: string
    getnRate?: string
    gettRate?: string
    getfRate?: string
    getjRate?: string
    getpRate?: string
    name?: string
    disc?: string
  }

  type MbtiTopic = {
    id?: number
    name?: string
    optionA?: string
    optionB?: string
    sequenceNumber?: number
    valA?: string
    valB?: string
  }

  type MentalAnalysisResult = {
    summary?: string
    metrics?: MentalHealthMetrics
    keywords?: string[]
    visualization?: DisplayRecommendation
  }

  type MentalHealthMetrics = {
    emotionalStability?: number
    socialEngagement?: number
    stressLevel?: number
  }

  type ModifyUserDTO = {
    email?: string
    password?: string
    checkPassword?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    phone?: string
    schoolName?: string
    province?: string
    sex?: string
  }

  type ModulesPageVO = {
    id?: number
    title?: string
    introductions?: string
    src?: string
  }

  type NewKnowledgeDTO = {
    title?: string
    descriptions?: string
    coverImage?: string
    tags?: string
    color?: string
    content?: string
    createId?: number
    mainNotNull?: boolean
  }

  type NootBook = {
    id?: number
    text?: string
    userId?: number
    title?: string
    createTime?: string
    updateTime?: string
  }

  type NootBookDTO = {
    pageNo?: number
    pageSize?: number
    isAsc?: boolean
    sortBy?: string
    userId?: number
  }

  type PageDTO = {
    pageNo?: number
    pageSize?: number
    isAsc?: boolean
    sortBy?: string
  }

  type PageResult = {
    total?: number
    records?: Record<string, any>[]
  }

  type Province = {
    id?: number
    name?: string
  }

  type PsychologicalTest = {
    id?: number
    question?: string
    optionsA?: string
    optionsB?: string
    optionsC?: string
    optionsD?: string
    optionsE?: string
    valA?: number
    valB?: number
    valC?: number
    valD?: number
    valE?: number
  }

  type publishCardParams = {
    swiperDTO: SwiperDTO
  }

  type queryBlogByGeoParams = {
    current?: number
    latitude?: number
    longitude?: number
  }

  type queryById1Params = {
    id: number
  }

  type queryByIdParams = {
    id: number
  }

  type queryFollowListParams = {
    current?: number
  }

  type queryLikedUserParams = {
    id: number
  }

  type queryMyLikeParams = {
    current?: number
  }

  type queryUserByGeoParams = {
    latitude?: number
    longitude?: number
  }

  type queryUserdataMessageParams = {
    userId: number
  }

  type queryUserDateParams = {
    userId: number
  }

  type QuestionSubmitDTO = {
    ans?: string[]
    moduleId?: number
  }

  type QuestionVO = {
    questionNumber?: number
    question?: string
    options?: string[]
    val?: string[]
  }

  type RegisterDTO = {
    password?: string
    checkPassword?: string
    userName?: string
    userAvatar?: string
    email?: string
    code?: string
    avatars?: string
  }

  type Result = {
    code?: number
    msg?: string
    data?: Record<string, any>
  }

  type ResultAnswerVO = {
    code?: number
    msg?: string
    data?: AnswerVO
  }

  type ResultBody = {
    code?: number
    msg?: string
    data?: Body
  }

  type ResultBodyReportVO = {
    code?: number
    msg?: string
    data?: BodyReportVO
  }

  type ResultGetPCCDatailVO = {
    code?: number
    msg?: string
    data?: GetPCCDatailVO
  }

  type ResultHistoryTestReportVO = {
    code?: number
    msg?: string
    data?: HistoryTestReportVO
  }

  type ResultListGetPCCVO = {
    code?: number
    msg?: string
    data?: GetPCCVO[]
  }

  type ResultListHistoryTestPageVO = {
    code?: number
    msg?: string
    data?: HistoryTestPageVO[]
  }

  type ResultListHotArticleVO = {
    code?: number
    msg?: string
    data?: HotArticleVO[]
  }

  type ResultListMbtiTopic = {
    code?: number
    msg?: string
    data?: MbtiTopic[]
  }

  type ResultListModulesPageVO = {
    code?: number
    msg?: string
    data?: ModulesPageVO[]
  }

  type ResultListProvince = {
    code?: number
    msg?: string
    data?: Province[]
  }

  type ResultListPsychologicalTest = {
    code?: number
    msg?: string
    data?: PsychologicalTest[]
  }

  type ResultListQuestionVO = {
    code?: number
    msg?: string
    data?: QuestionVO[]
  }

  type ResultListUniversities = {
    code?: number
    msg?: string
    data?: Universities[]
  }

  type ResultLoginVO = {
    code?: number
    msg?: string
    data?: LoginVO
  }

  type ResultMapStringObject = {
    code?: number
    msg?: string
    data?: Record<string, any>
  }

  type ResultMbtiTestVO = {
    code?: number
    msg?: string
    data?: MbtiTestVO
  }

  type ResultMentalAnalysisResult = {
    code?: number
    msg?: string
    data?: MentalAnalysisResult
  }

  type ResultObject = {
    code?: number
    msg?: string
    data?: Record<string, any>
  }

  type ResultRunning = {
    code?: number
    msg?: string
    data?: Running
  }

  type ResultRunningReportVO = {
    code?: number
    msg?: string
    data?: RunningReportVO
  }

  type ResultSmartwatchReportVO = {
    code?: number
    msg?: string
    data?: SmartwatchReportVO
  }

  type ResultSmartWatchVO = {
    code?: number
    msg?: string
    data?: SmartWatchVO
  }

  type ResultString = {
    code?: number
    msg?: string
    data?: string
  }

  type ResultTestVO = {
    code?: number
    msg?: string
    data?: TestVO
  }

  type ResultUserVO = {
    code?: number
    msg?: string
    data?: UserVO
  }

  type Running = {
    id?: number
    diatance?: number
    createTime?: string
    longTime?: number
    userId?: number
    equipId?: string
  }

  type RunningReportVO = {
    dateList?: string
    longTimeList?: string
    distanceCountList?: string
  }

  type searchByContentParams = {
    text?: string
    circleId?: number
    current?: number
  }

  type searchByTagParams = {
    tag?: string
    circleId?: number
    current?: number
  }

  type selectAll1Params = {
    current?: number
  }

  type selectBlogByUserIdParams = {
    current?: number
  }

  type selectById1Params = {
    id: number
  }

  type selectById2Params = {
    id: number
  }

  type selectByIdParams = {
    id: number
  }

  type selectByUserIdParams = {
    userId: number
    current?: number
  }

  type selectFirstCommentsParams = {
    blogId: number
    current?: number
  }

  type selectMemoryParams = {
    memoryId: string
    startTime: string
  }

  type selectTwoCommentParams = {
    parentId: number
    current?: number
  }

  type ShoolByProvinceDTO = {
    pageNo?: number
    pageSize?: number
    provinceId?: number
  }

  type SmartwatchReportVO = {
    dateList?: string
    sleepTimeList?: string
    sedentaryTimeList?: string
    heartRateList?: string
    bodyTemperatureList?: string
  }

  type SmartWatchVO = {
    sleepTime?: string
    sedentaryTime?: string
    heartRate?: string
    bodyTemperature?: string
  }

  type SportDTO = {
    title?: string
    league?: string
    startTime?: number
    endTime?: number
    leagueId?: number
  }

  type streamFluxParams = {
    prompt: string
    memoryId: string
  }

  type streamFluxWebParams = {
    prompt: string
    memoryId: string
  }

  type streamParams = {
    prompt: string
  }

  type streamTokenParams = {
    prompt: string
  }

  type SwiperDTO = {
    url?: string
    name?: string
    text?: string
    link?: string
    createId?: number
    mainNotNull?: boolean
  }

  type TestVO = {
    relationship?: string
    study?: string
    campus?: string
    job?: string
    emotion?: string
    self?: string
    satisfaction?: string
    total?: string
    good?: string[]
    bad?: string[]
    judge?: string
  }

  type TimeSlot = {
    id?: number
    timeDesc?: string
    startTime?: string
    endTime?: string
    pccId?: number
    approximateTime?: string
  }

  type TokenStream = true

  type Universities = {
    id?: number
    name?: string
    provinceId?: string
  }

  type updatePlayParams = {
    id: number
  }

  type uploadParams = {
    file: string
  }

  type UserInfoDTO = {
    userAvatar: string
    userProfile: string
    phone: string
    sex: string
    province: string
  }

  type UserVO = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    roleId?: number
    school?: string
    studentNumber?: string
    email?: string
    sex?: string
  }

  type watchGraphParams = {
    scale: number
  }

  type watchParams = {
    sportId: number
  }

  type yeanParams = {
    name: string
  }
}
