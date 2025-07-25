import { Component } from '@angular/core';
import { TimesfivePipe } from '../pipes/timesfive.pipe';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [TimesfivePipe],
  templateUrl: './about.component.html',
  providers: [TimesfivePipe],
})
export class AboutComponent {}
