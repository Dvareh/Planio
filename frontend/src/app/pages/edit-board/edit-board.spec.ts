import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditBoard } from './edit-board';

describe('EditBoard', () => {
  let component: EditBoard;
  let fixture: ComponentFixture<EditBoard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditBoard],
    }).compileComponents();

    fixture = TestBed.createComponent(EditBoard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
