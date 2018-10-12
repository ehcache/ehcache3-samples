import { Moment } from 'moment';

export class WeatherReport {
    constructor(
        public date?: Moment,
        public location?: string,
        public icon?: string,
        public summary?: string,
        public temperatureMin?: number,
        public temperatureMax?: number
    ) {}
}

export class ResourceCallReport {
    constructor(public resourceName?: string, public resourceType?: string, public param?: string, public elapsed?: number) {}
}

export class Star {
    constructor(
        public id?: number,
        public firstName?: string,
        public lastName?: string,
        public birthDate?: Moment,
        public birthLocation?: string,
        public weatherReports?: WeatherReport[],
        public resourceCallReports?: ResourceCallReport[],
        public totalTimeSpent?: number,
        public hostname?: string
    ) {}
}
