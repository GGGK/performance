package performance.simulations.scenarios

import io.gatling.commons.validation._
import io.gatling.core.Predef._
import io.gatling.core.check._
import io.gatling.http.Predef._
import performance.simulations.lib.CommonHeader._
import performance.simulations.lib.JenkinsParam._

/**
  * Created by Tarun Kale
  */

class PostRequests extends Simulation {

  val headers_common = Map(
    "Content-Type" -> "application/json",
    "Accept" -> "application/json",
    "Authorization" -> "Bearer eyJraWQiOiIyMTIiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiYnpmUHREbTRmVHZsMm91YXF1bmlWZyIsInN1YiI6Imh0dHBzOi8vdGVzdC5zYWxlc2ZvcmNlLmNvbS9pZC8wMEQweDAwMDAwMDBOMnNFQUUvMDA1MHgwMDAwMDB5dG9DQUFRIiwiYXVkIjoiM01WRzlZYjVJZ3Fua0I0cUlCVUc3bHQ4RnEuS0FaVS5oX2hSWDlTR2t6eUZhb0tzbTdKTFouUFZLbnByOFNib2VEWGNLeVFvRzBVandCdklZQ1hEZyIsImlzcyI6Imh0dHBzOi8vcWEtcGhpbGFudGhyb3B5Y2xvdWQuY3M5NS5mb3JjZS5jb20vIiwiZXhwIjoxNTI0NjEyMDc0LCJpYXQiOjE1MjQ2MTE5NTQsIm5vbmNlIjoiMTUyNDYxMTkxNjkxOTAwIiwiY3VzdG9tX2F0dHJpYnV0ZXMiOnsiVXNlcm5hbWUiOiJ0a2FsZSt0ZXN0MUBzYWxlc2ZvcmNlLmNvbSJ9fQ.USUtTuBHCItCEYjIorGwkN4CqKzCo7psHMROTzrtAauLZOW5eLFEVRagZohnYRj8Kv1UsvTZxmYXDd8FfXOgg_5IKQQz3NnGd1f7eidHznLWldlCqMvYiF0orNhbc8MZJVQnw0IRRawTy0P607pykQUiydzWFpUUFa-_S9hEjxnobcsNdzk1mzW2MfLsP_pLFxkvKAfh7_Y3UjtG7t-NcI4EiUbYvH4Nzw4pUo2NLYTQKEk4B0f7VuZGVdN1wUfNWXRNgx3n1helspdbl0txxJ93z6mrsZ8kRdxziR1ZT-J2WauHVxRFsyx8AGH4ZSdoNEBtCSgN5CgzgA_kuic1f2cDJ0D2YePmYycafwXgReDIwZ8MQ3dNYPeBBsPufiZI5dR64SJNnXv9b0XQrfQ-gRlabaWT7_9aRe7atdNG_51rhEml16C9RlHwfLzf0udi2Q4jsZOfCygs0fNC_qj6DYkvAcsr2IW1-KPP2MlXntG7GaJZhGPUdiH56JHL6zBrpTCG5PVvA6WGG43AmB6Ji6mgillSAhSzxqiH-3KrdniQnHCR31kFd4QqkjV1lWxyfCdIeuEw4jygrTkxQE37ONPUj8wn84nOz8rnvEzt5mTE6eGHb1nkVA_KGFp3L38xlLPFEOcUIrKu0bY_Gr7tghROjtqZ3x9Djau-YkzCrvY"
  )

  def isNull[T] = new Validator[T] {
    val name = "isNull"
    def apply(actual : Option[T]) : Validation[Option[T]] = {
      actual match {
        case Some(null) => Success(actual)
        case _          => Failure("Value is not Null")
      }
    }
  }

  /* Load Tests on User Profile usecases*/

  val grpProfile = "UserProfile"

  // Query User Profile

  val scnQueryUserProfile = scenario("UserProfile").group(grpProfile) {
    feed(timestampFeeder)
      .exec(
        http("QueryUserProfile")
          .post("""/graphql""")
          .headers(headers_common)
          .body(StringBody(
            """{
                "query": "query{\n  me {\n    person {\n      id\n      firstName\n      lastName\n      jobTitle\n      email\n      phone\n      avatar\n      languagePreference\n      address\n      city\n      totalDonated\n      state\n      zip\n      unitedWay\n      organizations {\n        id\n        name\n        __typename\n      }\n      causes {\n        id\n        name\n        __typename\n      }\n      transactions {\n        id\n        date\n        amount\n        __typename\n      }\n    }\n    __typename\n  }\n}\n"
              }""")).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }

  /* Load Tests on Person usecases*/

  val grpPerson = "Person"

  // Is Person Active

  val scnIsPersonActive = scenario("IsPersonActive").group(grpPerson) {
    feed(timestampFeeder)
      .exec(
        http("IsPersonActive")
          .post("""/graphql""")
          .headers(headers_common)
          .body(StringBody(
            """{
                "query": "query{\n  isPersonActive(\n    email: \"testuser@salesforce.com.qa\"\n    \n  ){\n    email\n    pcActive\n    sfidActive\n  }\n    \n  \n}"
              }""")).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  /* Load Tests on Causes usecases */

  val grpCauses = "Causes"

  // List Causes
  val scnListCauses = scenario("ListCauses").group(grpCauses) {
    feed(timestampFeeder)
      .exec(
        http("ListCauses")
          .post("""/graphql""")
          .headers(headers_common)
          .body(StringBody(
            """{
                "query": "{\n  causes {\n    id\n    name\n    primaryImageCrid\n    primaryImageAlt\n    excerpt\n __typename\n  }\n}\n"
              }""")).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }

  // Update Pref Causes
  val scnUpdatePrefCauses = scenario("UpdatePrefCauses").group(grpCauses) {
    feed(timestampFeeder)
      .exec(
        http("UpdatePrefCauses")
          .post("""/graphql""")
          .headers(headers_common)
          .body(StringBody(
            """{
                "query": "mutation {\n        test: updatePrefCauses(\n          causes: {\n            personId: 1006,\n            causeIds: [11001, 11002, 11003]\n          }\n        ) {\n          person {\n            causes {\n              id\n            }\n          }\n        }\n      }"
              }""")).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  /* Load Tests on Sustainable Development Goals usecases */

  val grpSDG = "SDG"

  // List SDG
  val scnListSDG = scenario("ListSDG").group(grpSDG) {
    feed(timestampFeeder)
      .exec(
        http("ListSDG")
          .post("""/graphql""")
          .headers(headers_common)
          .body(StringBody(
            """{
                "query": "{\n  sustainableDevelopmentGoals {\n    id\n    name\n    primaryImageCrid\n    primaryImageAlt\n    excerpt\n __typename\n  }\n}\n"
              }""")).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  /* Load Tests on Story usecases */

  val grpStory = "Story"

  // Create Story
  val scnCreateStory = scenario("CreateStory").group(grpStory) {
    feed(timestampFeeder)
      .exec(
        http("CreateStory")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\"operationName\":\"SaveStory\",\"variables\":{\"orgId\":2175,\"StoryInput\":{\"publishDate\":\"2018-04-24T07:00:00.000Z\",\"expirationDate\":\"2018-05-13T07:00:00.000Z\",\"publishState\":\"Published\",\"tags\":{\"causeIds\":[\"2008\",\"2009\",\"2013\",\"2025\"],\"sdgIds\":[\"1015\",\"1017\",\"1003\",\"1013\"]},\"scope\":{\"workplace\":[{\"id\":2175}],\"country\":[\"US\"],\"whereRaised\":[],\"location\":[]},\"localized\":{\"locale\":\"en_US\",\"name\":\"PerfStory\",\"subheading\":\"PerfStory\",\"excerpt\":\"PerfStory\",\"ctaText\":\"Read\",\"description\":\"<p>PerfStory</p>\",\"primaryImageCrid\":\"f7eukopbls8txhlek6dp\"},\"relatedContent\":[{\"id\":\"659\"}]}},\"query\":\"mutation SaveStory($orgId: ID!, $storyId: ID, $StoryInput: StoryInput!) {\\n  story(orgId: $orgId, storyId: $storyId, input: $StoryInput) {\\n    ...EditStory\\n    __typename\\n  }\\n}\\n\\nfragment EditStory on Story {\\n  relatedContent {\\n    ...ContentCard\\n    __typename\\n  }\\n  tag: __typename\\n  id\\n  localized {\\n    locale\\n    name\\n    primaryImageCrid\\n    primaryImageAlt\\n    excerpt\\n    subheading\\n    ctaText\\n    featuredVideo\\n    description\\n    __typename\\n  }\\n  modifiedDate\\n  createdDate\\n  publishDate\\n  publishState\\n  expirationDate\\n  causes {\\n    id\\n    name\\n    __typename\\n  }\\n  sdgs {\\n    id\\n    name\\n    __typename\\n  }\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n  scope {\\n    workplace {\\n      id\\n      name\\n      __typename\\n    }\\n    country\\n    whereRaised {\\n      id\\n      name\\n      __typename\\n    }\\n    location {\\n      latitude\\n      longitude\\n      unit\\n      label\\n      radius\\n      __typename\\n    }\\n    __typename\\n  }\\n  modifiedBy {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\\nfragment ContentCard on Content {\\n  ... on Story {\\n    ...StoryCard\\n    __typename\\n  }\\n  ... on ImpactFund {\\n    ...ImpactFundCard\\n    __typename\\n  }\\n  ... on Campaign {\\n    ...CampaignCard\\n    __typename\\n  }\\n  ... on NpoPage {\\n    ...NpoPageCard\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment StoryCard on Story {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  subheading\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment ImpactFundCard on ImpactFund {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  subheading\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment CampaignCard on Campaign {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  subheading\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment NpoPageCard on NpoPage {\\n  id\\n  name\\n  tag: __typename\\n  excerpt\\n  subheading\\n  description\\n  primaryImageCrid\\n  primaryImageAlt\\n  ctaText\\n  category: primaryCauseName\\n}\\n\"}"
          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  // Get Story
  val scnGetStory = scenario("GetStory").group(grpStory) {
    feed(timestampFeeder)
      .exec(
        http("GetStory")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n\t\"query\": \"query {\\n      story(id: 1430204) {\\n        id\\n        publishDate\\n        publishState\\n        expirationDate\\n        name\\n        subheading\\n        excerpt\\n        featuredVideo\\n        description\\n        primaryImageCrid\\n        primaryImageAlt\\n        ctaText\\n        causes {\\n          id\\n        }\\n        sdgs {\\n          id\\n        }\\n        localized {\\n          locale\\n          name\\n          subheading\\n          excerpt\\n          featuredVideo\\n          description\\n          primaryImageCrid\\n          primaryImageAlt\\n          ctaText\\n        }\\n        relatedContent {\\n          ...ContentCard\\n          __typename\\n        }\\n      }\\n    }\\n\\n    fragment ContentCard on Content {\\n      ... on Story {\\n        ...StoryCard\\n        __typename\\n      }\\n      ... on ImpactFund {\\n        ...ImpactFundCard\\n        __typename\\n      }\\n      ... on Campaign {\\n        ...CampaignCard\\n        __typename\\n      }\\n      ... on NpoPage {\\n        ...NonprofitCard\\n        __typename\\n      }\\n      __typename\\n    }\\n\\n    fragment StoryCard on Story {\\n      id\\n      name\\n      __typename\\n    }\\n\\n    fragment ImpactFundCard on ImpactFund {\\n      id\\n      name\\n      __typename\\n    }\\n\\n    fragment CampaignCard on Campaign {\\n      id\\n      name\\n      __typename\\n    }\\n\\n    fragment NonprofitCard on NpoPage {\\n      id\\n      name\\n      __typename\\n    }\"\n}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  /* Load Tests on Campaign usecases */

  val grpCampaign = "Campaign"

  // Create Campaign
  val scnCreateCampaign = scenario("CreateCampaign").group(grpCampaign) {
    feed(timestampFeeder)
      .exec(
        http("CreateCampaign")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\"operationName\":\"SaveCampaign\",\"variables\":{\"orgId\":2175,\"input\":{\"publishDate\":\"2018-04-24T07:00:00.000Z\",\"expirationDate\":\"2018-05-13T07:00:00.000Z\",\"publishState\":\"Published\",\"tags\":{\"causeIds\":[\"2001\",\"2002\",\"2003\",\"2008\"],\"sdgIds\":[\"1001\",\"1010\",\"1013\",\"1003\"]},\"scope\":{\"workplace\":[{\"id\":2175}],\"country\":[\"US\"],\"whereRaised\":[],\"location\":[]},\"localized\":{\"locale\":\"en_US\",\"name\":\"PerfCampaign\",\"subheading\":\"PerfCampaign\",\"excerpt\":\"PerfCampaign\",\"ctaText\":\"Donate\",\"description\":\"<p>PerfCampaign</p>\",\"primaryImageCrid\":\"iglpxyxtm6qmmikxgpxj\"},\"oneTimeAskLadder\":null,\"recurringTimeAskLadder\":null,\"fundraisingGoal\":4200000,\"currency\":\"USD\",\"allowNpoDesignation\":true,\"allowSpecifiedFunds\":true,\"allowLocalizedFunds\":true,\"givingOptions\":{\"impactFundIds\":[\"900\"],\"npoPageIds\":[]}}},\"query\":\"mutation SaveCampaign($orgId: ID!, $campaignId: ID, $input: CampaignInput!) {\\n  campaign(orgId: $orgId, campaignId: $campaignId, input: $input) {\\n    ...EditCampaign\\n    __typename\\n  }\\n}\\n\\nfragment EditCampaign on Campaign {\\n  oneTimeAskLadder\\n  recurringTimeAskLadder\\n  fundraisingGoal\\n  impactFunds {\\n    id\\n    name\\n    __typename\\n  }\\n  npoPage {\\n    id\\n    name\\n    __typename\\n  }\\n  currency\\n  allowLocalizedFunds\\n  allowNpoDesignation\\n  allowSpecifiedFunds\\n  tag: __typename\\n  id\\n  localized {\\n    locale\\n    name\\n    primaryImageCrid\\n    primaryImageAlt\\n    excerpt\\n    subheading\\n    ctaText\\n    featuredVideo\\n    description\\n    __typename\\n  }\\n  modifiedDate\\n  createdDate\\n  publishDate\\n  publishState\\n  expirationDate\\n  causes {\\n    id\\n    name\\n    __typename\\n  }\\n  sdgs {\\n    id\\n    name\\n    __typename\\n  }\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n  scope {\\n    workplace {\\n      id\\n      name\\n      __typename\\n    }\\n    country\\n    whereRaised {\\n      id\\n      name\\n      __typename\\n    }\\n    location {\\n      latitude\\n      longitude\\n      unit\\n      label\\n      radius\\n      __typename\\n    }\\n    __typename\\n  }\\n  modifiedBy {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\"}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }

  // Get Campaign
  val scnGetCampaign = scenario("GetCampaign").group(grpCampaign) {
    feed(timestampFeeder)
      .exec(
        http("GetCampaign")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\"operationName\":\"ContentDetails\",\"variables\":{\"id\":1430199,\"orgId\":2175},\"query\":\"query ContentDetails($orgId: Int!, $id: ID!) {\\n  organization(id: $orgId) {\\n    id\\n    content(id: $id) {\\n      ... on NpoPage {\\n        tag: __typename\\n        id\\n        name\\n        excerpt\\n        description\\n        primaryImageCrid\\n        ctaText\\n        category\\n        ein\\n        bridgeId\\n        craCrn\\n        isActive\\n        city\\n        state\\n        country\\n        relatedContent {\\n          ...ContentCard\\n          __typename\\n        }\\n      }\\n      ... on Story {\\n        tag: __typename\\n        id\\n        name\\n        excerpt\\n        subheading\\n        description\\n        primaryImageCrid\\n        featuredVideo\\n        ctaText\\n        relatedContent {\\n          ...ContentCard\\n          __typename\\n        }\\n      }\\n      ... on ImpactFund {\\n        tag: __typename\\n        id\\n        name\\n        excerpt\\n        subheading\\n        description\\n        primaryImageCrid\\n        featuredVideo\\n        ctaText\\n      }\\n      ... on Campaign {\\n        tag: __typename\\n        id\\n        name\\n        excerpt\\n        subheading\\n        description\\n        primaryImageCrid\\n        featuredVideo\\n        ctaText\\n        fundraisingGoal\\n        currency\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment ContentCard on Content {\\n  ... on Story {\\n    ...StoryCard\\n    __typename\\n  }\\n  ... on ImpactFund {\\n    ...ImpactFundCard\\n    __typename\\n  }\\n  ... on Campaign {\\n    ...CampaignCard\\n    __typename\\n  }\\n  ... on NpoPage {\\n    ...NpoPageCard\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment StoryCard on Story {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment ImpactFundCard on ImpactFund {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment CampaignCard on Campaign {\\n  tag: __typename\\n  id\\n  name\\n  primaryImageCrid\\n  primaryImageAlt\\n  excerpt\\n  category: primaryCauseName\\n  ctaText\\n}\\n\\nfragment NpoPageCard on NpoPage {\\n  id\\n  name\\n  tag: __typename\\n  excerpt\\n  description\\n  primaryImageCrid\\n  primaryImageAlt\\n  ctaText\\n  category: primaryCauseName\\n}\\n\"}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String])
      ))
  }


  /* Load Tests on Impact Fund usecases */

  val grpImpactFund = "ImpactFund"

  // Create Impact Fund
  val scnCreateImpactFund = scenario("CreateImpactFund").group(grpImpactFund) {
    feed(timestampFeeder)
      .exec(
        http("CreateImpactFund")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\"operationName\":\"saveImpactFund\",\"variables\":{\"orgId\":2175,\"input\":{\"publishDate\":\"2018-04-24T07:00:00.000Z\",\"expirationDate\":\"2018-05-13T07:00:00.000Z\",\"publishState\":\"Published\",\"tags\":{\"causeIds\":[\"2001\",\"2003\",\"2004\",\"2008\"],\"sdgIds\":[\"1010\",\"1014\",\"1015\",\"1003\"]},\"scope\":{\"workplace\":[{\"id\":2175}],\"country\":[\"US\"],\"whereRaised\":[],\"location\":[]},\"localized\":{\"locale\":\"en_US\",\"name\":\"PerfImpactFund\",\"subheading\":\"PerfImpactFund\",\"excerpt\":\"PerfImpactFund\",\"ctaText\":\"Donate\",\"description\":\"<p>PerfImpactFund</p>\",\"primaryImageCrid\":\"oblnsadjezkorcvic4ci\"},\"accountingCode\":\"PerfFund\"}},\"query\":\"mutation saveImpactFund($orgId: ID!, $impactFundId: ID, $input: ImpactFundInput!) {\\n  impactFund(orgId: $orgId, impactFundId: $impactFundId, input: $input) {\\n    ...EditImpactFund\\n    __typename\\n  }\\n}\\n\\nfragment EditImpactFund on ImpactFund {\\n  accountingCode\\n  tag: __typename\\n  id\\n  localized {\\n    locale\\n    name\\n    primaryImageCrid\\n    primaryImageAlt\\n    excerpt\\n    subheading\\n    ctaText\\n    featuredVideo\\n    description\\n    __typename\\n  }\\n  modifiedDate\\n  createdDate\\n  publishDate\\n  publishState\\n  expirationDate\\n  causes {\\n    id\\n    name\\n    __typename\\n  }\\n  sdgs {\\n    id\\n    name\\n    __typename\\n  }\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n  scope {\\n    workplace {\\n      id\\n      name\\n      __typename\\n    }\\n    country\\n    whereRaised {\\n      id\\n      name\\n      __typename\\n    }\\n    location {\\n      latitude\\n      longitude\\n      unit\\n      label\\n      radius\\n      __typename\\n    }\\n    __typename\\n  }\\n  modifiedBy {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\"}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }


  // Get Impact Fund
  val scnGetImpactFund = scenario("GetImpactFund").group(grpImpactFund) {
    feed(timestampFeeder)
      .exec(
        http("GetImpactFund")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"query {\\n           organization(\\n              id: 2175\\n             )\\n          {\\n          id,\\n            selectableContent(q:\\\"Building financial assets for families\\\"){\\n          __typename ... on ImpactFund {\\n          id,name\\n          }\\n          }\\n        }\\n      }\"\n}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }


  /* Load Tests on Organization usecases */

  val grpOrganization = "Organization"

  // Get Organization
  val scnGetOrganization = scenario("GetOrganization").group(grpOrganization) {
    feed(timestampFeeder)
      .exec(
        http("GetOrganization")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"query{\\n  test: organization(id: 2174) {\\n    id\\n    type\\n    roles\\n    name\\n    dba\\n    externalId\\n    street\\n    street2\\n    city\\n    state\\n    postalCode\\n    postalCodeExt\\n    country\\n    phone\\n    website\\n    defaultLanguage\\n    defaultCurrency\\n    timeZone\\n    brandSettings {\\n      id\\n      communityLogo\\n      communityLogoCrid\\n      primaryColor\\n      secondaryColor\\n      footerContent1\\n      footerContent2\\n      footerContent3\\n    }\\n    homepageSettings {\\n      id\\n      organizationMessage {\\n        isHidden\\n        message\\n        metrics {\\n          label\\n          metric\\n        }\\n      }\\n      givingBaseline\\n      heroes {\\n        __typename\\n        ... on Story {\\n          id\\n          createdDate\\n          modifiedDate\\n          publishDate\\n          publishState\\n          expirationDate\\n          author {\\n            id\\n            firstName\\n            lastName\\n            email\\n            city\\n            gender\\n          }\\n        }\\n        __typename\\n        ... on Campaign {\\n          id\\n          createdDate\\n          modifiedDate\\n          publishDate\\n          publishState\\n          expirationDate\\n          author {\\n            id\\n            firstName\\n            lastName\\n            email\\n            city\\n            gender\\n          }\\n        }\\n        __typename\\n        ... on ImpactFund {\\n          id\\n          createdDate\\n          modifiedDate\\n          publishDate\\n          publishState\\n          expirationDate\\n          author {\\n            id\\n            firstName\\n            lastName\\n            email\\n            city\\n            gender\\n          }\\n        }\\n        __typename\\n        ... on NpoPage {\\n          id\\n        }\\n      }\\n    }\\n    selectableContent(q: \\\"st\\\") {\\n      __typename\\n      ...on Campaign {\\n        id\\n        name\\n        excerpt\\n        primaryImageCrid\\n        primaryImageAlt\\n        ctaText        \\n      }\\n      ...on ImpactFund {\\n        id\\n        name\\n        excerpt\\n        primaryImageCrid\\n        primaryImageAlt\\n        ctaText        \\n      }\\n      ...on NpoPage {\\n        id\\n        name\\n        excerpt\\n        primaryImageCrid\\n        primaryImageAlt\\n        ctaText        \\n      }\\n      ...on Story {\\n        id\\n        name\\n        excerpt\\n        primaryImageCrid\\n        primaryImageAlt\\n        ctaText        \\n      }    \\n    }\\n  }\\n}\\n\"\n}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }


  /* Load Tests on Settings usecases */

  val grpSettings = "Settings"

  // Save Home Page Settings
  val scnHomePageSettings = scenario("HomePageSettings").group(grpSettings) {
    feed(timestampFeeder)
      .exec(
        http("HomePageSettings")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"mutation {\\n      test2: saveHomepageSettings(\\n        orgId: 2175\\n        homePageSettings: {\\n          featuredCards: {\\n            isHidden: false\\n            cards: [\\n              {\\n                id: 2\\n              }\\n              {\\n                id: 3\\n              }\\n            ]\\n          }\\n          heroes:[\\n            {\\n              id: 3\\n            }\\n            {\\n              id: 4\\n            }\\n            {\\n              id: 5\\n            }\\n          ]\\n          organizationMessage: {\\n            isHidden: true\\n              metrics: [\\n              {\\n                label: \\\"energy usage from renewable sources 2\\\"\\n                metric: \\\"14%\\\"\\n              }\\n              {\\n                label: \\\"cans of disaster relief drinking water donated 2\\\"\\n                metric: \\\"18M\\\"\\n              }\\n            ]\\n            message: \\\"Homepage Message Goes Here 2\\\"\\n          }\\n          givingBaseline: 456789\\n        }\\n      )\\n      {\\n        id\\n        name\\n        homepageSettings {\\n          heroes {\\n            __typename\\n          }\\n          featuredCards {\\n            isHidden\\n            cards {\\n              __typename\\n            }\\n          }\\n          organizationMessage {\\n            isHidden\\n            message\\n            metrics {\\n              label\\n              metric\\n            }\\n          }\\n          givingBaseline\\n        }\\n      }\\n    }\"\n}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }


  // Save Brand Settings
  val scnBrandSettings = scenario("BrandSettings").group(grpSettings) {
    feed(timestampFeeder)
      .exec(
        http("BrandSettings")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"mutation {\\n        test2: saveBrandSettings(\\n          orgId: 2175\\n          brandSettings: {\\n            communityLogo: \\\"blah\\\"\\n            primaryColor: \\\"#455667\\\"\\n            secondaryColor: \\\"#566778\\\"\\n            footerContent1: \\\"Rich Text Content 1\\\"\\n            footerContent2: \\\"Rich Text Content 2\\\"\\n            footerContent3: \\\"Rich Text Content 3\\\"\\n          }\\n        )\\n        {\\n          id\\n          name\\n          dba\\n          brandSettings {\\n            communityLogo\\n            primaryColor\\n            secondaryColor\\n            footerContent1\\n            footerContent2\\n            footerContent3\\n          }\\n        }\\n      }\"\n}"
          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }


  /* Load Tests on Settings usecases */

  val grpContentListing = "ContentListing"

  // Save Home Page Settings
  val scnContentListing = scenario("QueryContentListing").group(grpContentListing) {
    feed(timestampFeeder)
      .exec(
        http("QueryContentListing")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"query{\\n  contentListing(orgId: 2174, query: {keywords: \\\"ContentList\\\", maxResults: 1000, offset: 0}) {\\n    total\\n    maxResults\\n    offset\\n    content {\\n      ...CMSList\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment CMSList on Content {\\n  ... on Story {\\n    ...StoryCMSList\\n    __typename\\n  }\\n  ... on ImpactFund {\\n    ...ImpactFundCMSList\\n    __typename\\n  }\\n  ... on Campaign {\\n    ...CampaignCMSList\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment StoryCMSList on Story {\\n  tag: __typename\\n  id\\n  name\\n  modifiedDate\\n  publishState\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\\nfragment ImpactFundCMSList on ImpactFund {\\n  tag: __typename\\n  id\\n  name\\n  modifiedDate\\n  publishState\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\\nfragment CampaignCMSList on Campaign {\\n  tag: __typename\\n  id\\n  name\\n  modifiedDate\\n  publishState\\n  author {\\n    id\\n    firstName\\n    lastName\\n    __typename\\n  }\\n}\\n\"\n}"

          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }



  /* Load Tests on Npo Page usecases */

  /*val grpNpoPage = "NpoPage"

  // Create NPO page
  val scnCreateNpoPage = scenario("CreateNpoPage").group(grpNpoPage) {
    feed(timestampFeeder)
      .exec(
        http("CreateNpoPage")
          .post("/graphql")
          .headers(headers_common)
          .body(StringBody(
            "{\n  \"query\": \"mutation {\\n test: createNpoPage\\n (orgId: 1000,\\n   input:{\\n         localized: {\\n           locale: \\\"en_US\\\"\\n           name: \\\"NPOPage 1\\\"\\n           subheading: \\\"Campaign SH\\\"\\n           description: \\\"Blah Blah Blah\\\"\\n           primaryImageCrid: \\\"TestId\\\"\\n           primaryImageAlt: \\\"TestId\\\"\\n         }\\n   }) {\\n   id\\n   name\\n   description\\n   primaryImageAlt\\n   primaryImageCrid\\n\\n } \\n}\"\n}"
          )).asJSON
          .check(status.is(200))
          .check(jsonPath("$.errors").validate(isNull[String]))
      )
  }*/

}
